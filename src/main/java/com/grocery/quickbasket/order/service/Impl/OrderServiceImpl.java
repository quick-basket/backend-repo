package com.grocery.quickbasket.order.service.Impl;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.midtrans.service.MidtransService;
import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.OrderListResponseDto;
import com.grocery.quickbasket.order.dto.OrderResponseDto;
import com.grocery.quickbasket.order.dto.OrderWithMidtransResponseDto;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderItem;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderItemRepository;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.dto.UserAddressDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.entity.UserAddress;
import com.grocery.quickbasket.user.service.UserAddressService;
import com.grocery.quickbasket.user.service.UserService;
import com.midtrans.httpclient.error.MidtransError;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final UserAddressService addressService;
    private final CartService cartService;
    private final StoreService storeService;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final MidtransService midtransService;

    // Inject Midtrans configuration
    @Value("${midtrans.server.key}")
    private String midtransServerKey;

    @Value("${midtrans.client.key}")
    private String midtransClientKey;

    public OrderServiceImpl(UserService userService, OrderRepository orderRepository, UserAddressService addressService, CartService cartService, StoreService storeService, ProductRepository productRepository, StoreRepository storeRepository, MidtransService midtransService) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.addressService = addressService;
        this.cartService = cartService;
        this.storeService = storeService;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.midtransService = midtransService;
    }

    @Override
    public CheckoutDto createCheckoutSummaryFromCart() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        CheckoutDto checkoutDto = new CheckoutDto();

        checkoutDto.setUserId(userId);

        Store store = storeRepository.findById(1L)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));
        checkoutDto.setStoreId(1L);
        checkoutDto.setStoreName(store.getName());

        //add recipient
        User user = userService.findById(userId);
        CheckoutDto.Recipient recipient = new CheckoutDto.Recipient();
        recipient.setName(user.getName());
        recipient.setPhone(user.getPhone());

        UserAddress userAddress = UserAddressDto.toEntity(addressService.getPrimaryAddress());
        recipient.setAddressId(userAddress.getId());
        recipient.setCity(userAddress.getCity());
        recipient.setFullAddress(userAddress.getAddress());
        recipient.setPostalCode(userAddress.getPostalCode());
        checkoutDto.setRecipient(recipient);

        //add items
        List<CartListResponseDto> itemListFromCart = cartService.getAllCartByUserIdWithStoreId(1L);
        List<CheckoutDto.Item> itemList = itemListFromCart.stream()
                .map(cartItem -> {
                    CheckoutDto.Item item = new CheckoutDto.Item();
                    item.setProductId(cartItem.getProductId());
                    item.setName(cartItem.getProductName());
                    item.setPrice(cartItem.getPrice());
                    item.setDiscountPrice(cartItem.getDiscountPrice());
                    item.setQuantity(cartItem.getQuantity());
                    item.setSubtotal(cartItem.getPrice().subtract(cartItem.getDiscountPrice()));
                    item.setImage(cartItem.getImageUrls().isEmpty() ? "/api/placeholder/50/50" : cartItem.getImageUrls().getFirst());
                    return item;
                })
                .toList();
        checkoutDto.setItems(itemList);

        CartSummaryResponseDto cartSummary = cartService.getCartSummary(checkoutDto.getStoreId());
        CheckoutDto.Summary summary = new CheckoutDto.Summary();
        summary.setSubtotal(cartSummary.getTotalPrice());
        summary.setDiscount(cartSummary.getTotalDiscount());
        summary.setTotal(cartSummary.getTotalDiscountPrice());
        summary.setShippingCost(BigDecimal.valueOf(5000));
        checkoutDto.setSummary(summary);

        return checkoutDto;

    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return new OrderResponseDto().mapToDto(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (order.getStatus().canBeCancelled()) {
            order.setStatus(OrderStatus.CANCELED);
            return orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order cannot be cancelled in its current state");
        }
    }

    @Override
    public List<Order> getUserOrders() {
        return List.of();
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public Order createOrderFromCheckoutData(CheckoutDto checkoutData) throws MidtransError {
        Order order = new Order();

        //set user
        User user = userService.getCurrentUser();
        order.setUserId(user.getId());

        // set store
        Store store = StoreDto.toEntity(storeService.getStoreById(checkoutData.getStoreId()));
        order.setStore(store);

        //set shipping address
        UserAddress shippingAddress = UserAddressDto.toEntity(addressService.getPrimaryAddress());
        order.setShippingAddress(shippingAddress);

        BigDecimal shippingCost = checkoutData.getSummary().getShippingCost();

        // set order details
        order.setTotalAmount(checkoutData.getSummary().getTotal().add(shippingCost));
        order.setShippingCost(shippingCost);
        order.setTotalAmountDiscount(checkoutData.getSummary().getDiscount());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        //Order items
        List<OrderItem> orderItems = checkoutData.getItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found")));
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getDiscountPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public List<OrderListResponseDto> getAllOrderByStoreIdAndUserId(Long storeId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<Order> orders = orderRepository.findByStoreIdAndUserId(storeId, userId);
        return orders.stream()
                .map(OrderListResponseDto::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderWithMidtransResponseDto createOrRetrievePendingOrder(CheckoutDto checkoutData, String paymentType) throws MidtransError {
        User currentUser = userService.getCurrentUser();
        Store store = storeRepository.findById(checkoutData.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));

        Optional<Order> existingPendingOrder = orderRepository.findTopByUserIdAndStoreAndStatusOrderByCreatedAtDesc(
                currentUser.getId(), store, OrderStatus.PENDING_PAYMENT
        );

        Order order;
        Map<String, Object> midtransResponseMap;

        if (existingPendingOrder.isPresent()) {
            order = existingPendingOrder.get();
            if (isOrderOlderThan24Hours(order)) {
                cancelOrder(order.getId());
                order = createOrderFromCheckoutData(checkoutData);
            }
        } else {
            order = createOrderFromCheckoutData(checkoutData);
        }

        midtransResponseMap = midtransService.createOrRetrieveMidtransTransaction(order, checkoutData, paymentType);

        // Update order status based on Midtrans response
        String midtransStatus = (String) midtransResponseMap.get("transaction_status");
        String fraudStatus = (String) midtransResponseMap.get("fraud_status");
        updateOrderStatusBasedOnMidtransStatus(order, midtransStatus, fraudStatus);

        order = orderRepository.save(order);
        OrderResponseDto orderResponseDto = new OrderResponseDto().mapToDto(order);
        return new OrderWithMidtransResponseDto(orderResponseDto, midtransResponseMap);
    }

    @Override
    public OrderResponseDto updateOrderStatusAfterPayment(String orderId, String paymentStatus) throws MidtransError {
        // Find the order by the orderId string
        Order order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found for code: " + orderId));

        log.info("Updating payment status for order: {} with status: {}", orderId, paymentStatus);

        String normalizedStatus = paymentStatus.trim().toLowerCase();

        switch (normalizedStatus) {
            case "capture":
            case "settlement":
                order.setStatus(OrderStatus.PROCESSING);
                break;
            case "pending":
                order.setStatus(OrderStatus.PENDING_PAYMENT);
                break;
            case "deny":
            case "cancel":
            case "expire":
                order.setStatus(OrderStatus.CANCELED);
                break;
            default:
                log.warn("Unhandled payment status: {} for order: {}", paymentStatus, orderId);
                break;
        }

        order = orderRepository.save(order);
        return new OrderResponseDto().mapToDto(order);
    }

    @Override
    public OrderResponseDto getOrderStatus(Long orderId) throws MidtransError {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        // Fetch the latest status from Midtrans
        JSONObject transactionStatus = midtransService.getTransactionStatus(order.getOrderCode());
        String midtransStatus = transactionStatus.getString("transaction_status");
        String fraudStatus = transactionStatus.optString("fraud_status");

        updateOrderStatusBasedOnMidtransStatus(order, midtransStatus, fraudStatus);
        order = orderRepository.save(order);

        return new OrderResponseDto().mapToDto(order);
    }


    private boolean isOrderOlderThan24Hours(Order order) {
        return order.getCreatedAt().isBefore(Instant.now().minus(24, ChronoUnit.HOURS));
    }

    private void updateOrderStatusBasedOnMidtransStatus(Order order, String transactionStatus, String fraudStatus) {
        switch (transactionStatus) {
            case "capture":
            case "settlement":
                if ("challenge".equals(fraudStatus)) {
                    order.setStatus(OrderStatus.PAYMENT_CONFIRMATION);
                } else if ("accept".equals(fraudStatus)) {
                    order.setStatus(OrderStatus.PROCESSING);
                }
                break;
            case "pending":
                order.setStatus(OrderStatus.PENDING_PAYMENT);
                break;
            case "deny":
            case "cancel":
            case "expire":
                order.setStatus(OrderStatus.CANCELED);
                break;
            default:
                // Keep the current status if unknown
                break;
        }
    }
    @Override
    public BigDecimal getTotalAmountAllStore() {
        return orderItemRepository.sumTotalAmountFromAllOrders();
    }
    @Override
    public BigDecimal getTotalAmountFromOrdersLastWeek() {
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        return orderItemRepository.sumTotalAmountFromOrdersLastWeek(oneWeekAgo);
    }
    @Override
    public BigDecimal getTotalAmountFromOrdersLastMonth() {
        Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        return orderItemRepository.sumTotalAmountFromOrdersLastMonth(oneMonthAgo);
    }
    @Override
    public BigDecimal getTotalAmountByStoreAndCategory(Long storeId, Long categoryId) {
        return orderItemRepository.getTotalAmountByStoreAndCategory(storeId, categoryId);
    }
    @Override
    public BigDecimal getTotalAmountByStoreId(Long storeId) {
        return orderItemRepository.getTotalAmountByStore(storeId);
    }

    @Override
    public BigDecimal getTotalAmountByStoreAndProduct(Long storeId, Long productId) {
        return orderItemRepository.getTotalAmountByStoreAndProduct(storeId, productId);
    }
}
