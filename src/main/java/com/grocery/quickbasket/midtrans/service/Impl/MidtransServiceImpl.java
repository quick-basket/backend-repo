package com.grocery.quickbasket.midtrans.service.Impl;

import com.grocery.quickbasket.midtrans.repository.MidtransRedisRepository;
import com.grocery.quickbasket.midtrans.service.MidtransService;
import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.mapper.MapperHelper;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.user.service.UserService;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MidtransServiceImpl implements MidtransService {

    private final MidtransCoreApi coreApi;
    private final UserService userService;
    private final OrderService orderService;
    private final MidtransRedisRepository midtransRedisRepository;

    public MidtransServiceImpl(@Value("${midtrans.server.key}") String serverKey,
                               @Value("${midtrans.client.key}") String clientKey,
                               @Value("${midtrans.is.production}") boolean isProduction, @Lazy UserService userService, @Lazy OrderService orderService, MidtransRedisRepository midtransRedisRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.midtransRedisRepository = midtransRedisRepository;
        Config config = Config.builder()
                .setServerKey(serverKey)
                .setClientKey(clientKey)
                .setIsProduction(isProduction)
                .build();
        this.coreApi = new ConfigFactory(config).getCoreApi();
    }


    @Override
    public JSONObject createTransaction(Map<String, Object> transactionDetails) throws MidtransError {
        return coreApi.chargeTransaction(transactionDetails);
    }

    @Override
    public JSONObject getTransactionStatus(String orderId) throws MidtransError {
        return coreApi.checkTransaction(orderId);
    }

    @Override
    public Map<String, Object> buildMidtransRequest(Order order, CheckoutDto checkoutData, String paymentType) {
        Map<String, Object> params = new HashMap<>();

        BigDecimal totalAmount = order.getTotalAmount();

        // Set payment type based on the input
        params.put("payment_type", getPaymentType(paymentType));

        // Add payment method specific details
        addPaymentMethodDetails(params, paymentType);

        params.put("transaction_details", new HashMap<String, String>() {{
            put("order_id", order.getOrderCode());
            put("gross_amount", totalAmount.toString());
        }});

        List<Map<String, String>> itemDetails = checkoutData.getItems().stream()
                .map(item -> new HashMap<String, String>() {{
                    put("id", item.getProductId().toString());
                    put("price", item.getDiscountPrice().toString());
                    put("quantity", String.valueOf(item.getQuantity()));
                    put("name", item.getName());
                }})
                .collect(Collectors.toList());

        // Add shipping cost as a separate item
        BigDecimal shippingCost = checkoutData.getSummary().getShippingCost();
        Map<String, String> shippingItem = new HashMap<>();
        shippingItem.put("id", "SHIPPING");
        shippingItem.put("price", shippingCost.toString());
        shippingItem.put("quantity", "1");
        shippingItem.put("name", "Shipping Cost");
        itemDetails.add(shippingItem);

        params.put("item_details", itemDetails);

        CheckoutDto.Recipient recipient = checkoutData.getRecipient();
        params.put("customer_details", new HashMap<String, Object>() {{
            put("first_name", recipient.getName());
            put("email", userService.getCurrentUser().getEmail());
            put("phone", recipient.getPhone());
            put("shipping_address", new HashMap<String, String>() {{
                put("first_name", recipient.getName());
                put("phone", recipient.getPhone());
                put("address", recipient.getFullAddress());
                put("city", recipient.getCity());
                put("postal_code", recipient.getPostalCode());
            }});
        }});

        params.put("callbacks", new HashMap<String, String>() {{
            put("finish", "http://localhost:3000/checkout");
            put("unfinish", "http://localhost:3000/checkout");
            put("error", "http://localhost:3000/checkout");
        }});

        log.info("Params: " + params);
        return params;
    }

    private String getPaymentType(String paymentType) {
        return switch (paymentType.toLowerCase()) {
            case "bca", "bni" -> "bank_transfer";
            case "gopay" -> "gopay";
            default -> throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
        };
    }

    private void addPaymentMethodDetails(Map<String, Object> params, String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "bca":
            case "bni":
                params.put("bank_transfer", new HashMap<String, Object>() {{
                    put("bank", paymentType.toLowerCase());
                }});
                break;
            case "gopay":
                params.put("gopay", new HashMap<String, Object>() {{
                    put("enable_callback", true);
                    put("callback_url", "http://localhost:3000/gopay-callback");
                }});
                break;
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
        }
    }

    @Override
    public Map<String, Object> createOrRetrieveMidtransTransaction(Order order, CheckoutDto checkoutData, String paymentType) throws MidtransError {
        JSONObject midtransResponse;

        if (order.getMidtransTransactionId() == null) {
            // New transaction
            Map<String, Object> params = buildMidtransRequest(order, checkoutData, paymentType);
            log.info("Initiating Midtrans transaction for order: {} with params: {}", order.getOrderCode(), params);

            try {
                midtransResponse = createTransaction(params);
                log.info("Midtrans transaction created successfully for order: {} with response: {}", order.getOrderCode(), midtransResponse);

                // Update order with Midtrans transaction ID
                String midtransTransactionId = midtransResponse.getString("transaction_id");
                order.setMidtransTransactionId(midtransTransactionId);

                //save to redis
                midtransRedisRepository.saveMidtransResponse(midtransResponse);
            } catch (Exception e) {
                log.error("Error creating Midtrans transaction for order: {}. Error: {}", order.getOrderCode(), e.getMessage(), e);
                throw new MidtransError("Failed to create Midtrans transaction: " + e.getMessage());
            }
        } else {
            // Existing transaction, try to get from Redis first
            midtransResponse = midtransRedisRepository.getMidtransResponse(order.getMidtransTransactionId());

            if (midtransResponse == null) {
                // If not in Redis, get from Midtrans API
                midtransResponse = getTransactionStatus(order.getOrderCode());

                // Save to Redis for future use
                midtransRedisRepository.saveMidtransResponse(midtransResponse);
            }
        }

        return MapperHelper.jsonObjectToMap(midtransResponse);
    }

    @Override
    public void handleNotification(Map<String, Object> notificationPayload) throws MidtransError {
        log.info("Received Midtrans notification: {}", notificationPayload);

        String orderId = (String) notificationPayload.get("order_id");
        String transactionStatus = (String) notificationPayload.get("transaction_status");
        String fraudStatus = (String) notificationPayload.get("fraud_status");

        // Update order status
        try {
            orderService.updateOrderStatusAfterPayment(orderId, transactionStatus);
        } catch (Exception e) {
            log.error("Error updating order status for order {}: {}", orderId, e.getMessage());
            throw new MidtransError("Failed to update order status: " + e.getMessage());
        }

        // Delete Redis entry
        String transactionId = (String) notificationPayload.get("transaction_id");
        midtransRedisRepository.deleteMidtransResponse(transactionId);

        log.info("Successfully processed notification for order {}", orderId);
    }

}
