package com.grocery.quickbasket.order.service.Impl;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartListSummaryResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.exceptions.PendingOrderExcerption;
import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.exceptions.VoucherApplicationException;
import com.grocery.quickbasket.inventory.service.InventoryService;
import com.grocery.quickbasket.location.service.LocationService;
import com.grocery.quickbasket.midtrans.repository.MidtransRedisRepository;
import com.grocery.quickbasket.midtrans.service.MidtransService;
import com.grocery.quickbasket.order.dto.*;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderItem;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.mapper.MapperHelper;
import com.grocery.quickbasket.order.mapper.OrderMapper;
import com.grocery.quickbasket.order.repository.OrderItemRepository;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import com.grocery.quickbasket.payment.mapper.MapperHelperPayment;
import com.grocery.quickbasket.payment.service.PaymentService;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreWithDistanceDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.dto.UserAddressDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.entity.UserAddress;
import com.grocery.quickbasket.user.service.UserAddressService;
import com.grocery.quickbasket.user.service.UserService;
import com.grocery.quickbasket.vouchers.entity.DiscountTypes;
import com.grocery.quickbasket.vouchers.entity.UserVoucher;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.repository.UserVoucherRepository;
import com.midtrans.httpclient.error.MidtransError;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;
    private final MidtransRedisRepository midtransRedisRepository;
    private final OrderMapper orderMapper;
    private final UserVoucherRepository userVoucherRepository;
    private final LocationService locationService;
    private final InventoryService inventoryService;

    // Inject Midtrans configuration
    @Value("${midtrans.server.key}")
    private String midtransServerKey;

    @Value("${midtrans.client.key}")
    private String midtransClientKey;

    public OrderServiceImpl(UserService userService, OrderRepository orderRepository, UserAddressService addressService, CartService cartService, StoreService storeService, ProductRepository productRepository, StoreRepository storeRepository, MidtransService midtransService, OrderItemRepository orderItemRepository, UserVoucherRepository userVoucherRepository, MidtransRedisRepository midtransRedisRepository, OrderMapper orderMapper, PaymentService paymentService, LocationService locationService, InventoryService inventoryService) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.addressService = addressService;
        this.cartService = cartService;
        this.storeService = storeService;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.midtransService = midtransService;
        this.orderItemRepository = orderItemRepository;
        this.paymentService = paymentService;
        this.midtransRedisRepository = midtransRedisRepository;
        this.orderMapper = orderMapper;
        this.userVoucherRepository = userVoucherRepository;
        this.locationService = locationService;
        this.inventoryService = inventoryService;
    }

    @Override
    public CheckoutDto createCheckoutSummaryFromCart(Long storeId, Long userVoucherId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        CheckoutDto checkoutDto = new CheckoutDto();

        checkoutDto.setUserId(userId);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));
        checkoutDto.setStoreId(storeId);
        checkoutDto.setStoreName(store.getName());

        //add recipient
        User user = userService.findById(userId);
        CheckoutDto.Recipient recipient = new CheckoutDto.Recipient();
        recipient.setName(user.getName());
        recipient.setPhone(user.getPhone());

        UserAddressDto userAddress = addressService.getPrimaryAddress();
        recipient.setAddressId(userAddress.getId());
        recipient.setCity(userAddress.getCity());
        recipient.setFullAddress(userAddress.getAddress());
        recipient.setPostalCode(userAddress.getPostalCode());
        checkoutDto.setRecipient(recipient);

        //add items
        List<CartListResponseDto> itemListFromCart = cartService.getAllCartByUserIdWithStoreId(storeId);
        List<CheckoutDto.Item> itemList = itemListFromCart.stream()
                .map(cartItem -> {
                    CheckoutDto.Item item = new CheckoutDto.Item();
                    item.setProductId(cartItem.getProductId());
                    item.setInventoryId(cartItem.getInventoryId());
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

        CartSummaryResponseDto cartSummary = cartService.getCartSummary(storeId);
        CheckoutDto.Summary summary = new CheckoutDto.Summary();
        summary.setSubtotal(cartSummary.getTotalPrice());

        //calculate shipping cost
        StoreWithDistanceDto storeWithDistanceDto = locationService.findNearestStore(userAddress.getLongitude(), userAddress.getLatitude());
        log.info("store with distance: {}", storeWithDistanceDto);
        BigDecimal shippingCost = storeWithDistanceDto.getDeliveryCost();
        log.info("shipping cost: {}", shippingCost);
        summary.setShippingCost(shippingCost);

        BigDecimal totalBeforeVoucher = cartSummary.getTotalDiscountPrice();
        log.info("=========total voucher{}", totalBeforeVoucher.toString());

        // Apply voucher if provided
        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if (userVoucherId != null) {
            List<CartListSummaryResponseDto> cartListSummary = convertToCartListSummary(itemListFromCart);
            try {
                UserVoucher userVoucher = userVoucherRepository.findByIdAndUserIdAndIsUsedFalse(userVoucherId, userId)
                        .orElseThrow(() -> new DataNotFoundException("Voucher is not found"));

                Voucher voucher = userVoucher.getVoucher();

                if (!isVoucherValid(voucher, totalBeforeVoucher)) {
                    throw new IllegalArgumentException("Voucher its not meet criteria");
                }

                voucherDiscount = calculateVoucherDiscount(userId, userVoucherId, totalBeforeVoucher, cartListSummary);
                checkoutDto.setAppliedVoucherId(userVoucherId);
                checkoutDto.setAppliedVoucherCode(userVoucherRepository.findById(userVoucherId)
                        .map(uv -> uv.getVoucher().getCode())
                        .orElse(null));
            } catch (Exception e) {
                log.error("Error applying voucher: ", e);
                throw new VoucherApplicationException("Failed to apply voucher: " + e.getMessage());
            }
        }

        BigDecimal finalTotal = totalBeforeVoucher.subtract(voucherDiscount);
        summary.setDiscount(cartSummary.getTotalDiscount());
        summary.setVoucher(voucherDiscount);
        summary.setTotal(finalTotal);
        log.info("=========total finalll{}", finalTotal.toString());
        log.info("=========voucher discount{}", voucherDiscount.toString());

        checkoutDto.setSummary(summary);

        return checkoutDto;
    }

    private boolean isVoucherValid(Voucher voucher, BigDecimal totalBeforeVoucher) {
        Instant now = Instant.now();
        return now.isAfter(voucher.getStartDate()) && now.isBefore(voucher.getEndDate())
                && (voucher.getMinPurchase() == null || totalBeforeVoucher.compareTo(voucher.getMinPurchase()) >= 0);
    }

    private List<CartListSummaryResponseDto> convertToCartListSummary(List<CartListResponseDto> itemListFromCart) {
        return itemListFromCart.stream()
                .map(item -> {
                    CartListSummaryResponseDto summary = new CartListSummaryResponseDto();
                    summary.setProductId(item.getProductId());
                    summary.setPrice(item.getPrice());
                    summary.setQuantity(item.getQuantity());
                    // Set other fields as needed
                    return summary;
                })
                .collect(Collectors.toList());
    }

    private void applyVoucherToOrder(Order order, Long userVoucherId) {
        if (userVoucherId != null) {
            UserVoucher userVoucher = userVoucherRepository.findByIdAndIsUsedFalse(userVoucherId)
                    .orElseThrow(() -> new DataNotFoundException("Voucher is not found"));

            order.setVoucher(userVoucher);
//            userVoucher.setIsUsed(true);
            userVoucher.setUsedAt(Instant.now());
            userVoucherRepository.save(userVoucher);
        }
    }

    public BigDecimal calculateVoucherDiscount(Long userId, Long userVoucherId, BigDecimal totalPrice, List<CartListSummaryResponseDto> cartList) {
        UserVoucher userVoucher = userVoucherRepository.findByIdAndUserIdAndIsUsedFalse(userVoucherId, userId)
                .orElseThrow(() -> new DataNotFoundException("Voucher is not found"));

        Voucher voucher = userVoucher.getVoucher();

        Instant now = Instant.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())
                || (voucher.getMinPurchase() != null && totalPrice.compareTo(voucher.getMinPurchase()) < 0)) {
            throw new IllegalArgumentException("Voucher not meet criteria");
        }

        switch (voucher.getVoucherType()) {
            case CART_TOTAL:
            case REFERRAL:
                if (voucher.getDiscountType() == DiscountTypes.PERCENTAGE) {
                    BigDecimal discountPercentage = voucher.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    return totalPrice.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
                } else if (voucher.getDiscountType() == DiscountTypes.FIXED) {
                    return voucher.getDiscountValue().min(totalPrice);
                }
                break;

            case PRODUCT_SPECIFIC:
                if (voucher.getProduct() != null && voucher.getProduct().getId() != null) {
                    return cartList.stream()
                            .filter(cart -> cart.getProductId().equals(voucher.getProduct().getId()))
                            .map(cart -> {
                                BigDecimal itemPrice = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                                if (voucher.getDiscountType() == DiscountTypes.PERCENTAGE) {
                                    return itemPrice.multiply(voucher.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                                } else if (voucher.getDiscountType() == DiscountTypes.FIXED) {
                                    return voucher.getDiscountValue().multiply(BigDecimal.valueOf(cart.getQuantity())).min(itemPrice);
                                }
                                return BigDecimal.ZERO;
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                break;

            case SHIPPING:
                // Implement shipping discount logic here
                return BigDecimal.ZERO;

            default:
                throw new IllegalArgumentException("Voucher is invalid");
        }
        return BigDecimal.ZERO;
    }
    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return new OrderResponseDto().mapToDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto cancelOrder(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (!order.getStatus().canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in its current state");
        }

        order.setStatus(OrderStatus.CANCELED);

        // Update associated payment
        paymentService.updatePaymentStatus(orderCode, PaymentStatus.FAILED);

        // TODO: Handle inventory updates (return items to stock)

        orderRepository.save(order);
        return new OrderResponseDto().mapToDto(order);
    }

    @Override
    public OrderListDetailDto getUserOrders(int page, int size) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        log.info("order that get shown: {}", orderPage);

        return orderMapper.mapToOrderListResponseDto(orderPage);
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new DataNotFoundException("Order not found"));
    }

    @Override
    public OrderWithMidtransResponseDto getPendingOrder(Long userId) {
        Order order = orderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING_PAYMENT)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        OrderResponseDto dto = new OrderResponseDto().mapToDto(order);

        if (order.getMidtransTransactionId() == null) {
            return new OrderWithMidtransResponseDto(dto, null);
        }

        JSONObject midtransResponse = midtransRedisRepository.getMidtransResponse(order.getMidtransTransactionId());

        assert midtransResponse != null;
        Map<String, Object> midtransResponseMap = midtransResponse.toMap();

        return new OrderWithMidtransResponseDto(dto, midtransResponseMap);
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
    public OrderWithMidtransResponseDto createOrder(CheckoutDto checkoutData, String paymentType) throws MidtransError {
        User currentUser = userService.getCurrentUser();
        Store store = storeRepository.findById(checkoutData.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));

        Optional<Order> existingPendingOrder = orderRepository.findTopByUserIdAndStoreAndStatusOrderByCreatedAtDesc(
                currentUser.getId(), store, OrderStatus.PENDING_PAYMENT
        );
        if (existingPendingOrder.isPresent()) {
            throw new PendingOrderExcerption();
        }

        Order order = createOrderFromCheckoutData(checkoutData);

        applyVoucherToOrder(order, checkoutData.getAppliedVoucherId());

        Map<String, Object> midtransResponseMap = null;
        if (!paymentType.equalsIgnoreCase("manual")) {
            midtransResponseMap = midtransService.createOrRetrieveMidtransTransaction(order, checkoutData, paymentType);

            // Update order status based on Midtrans response
            String midtransStatus = (String) midtransResponseMap.get("transaction_status");
            String fraudStatus = (String) midtransResponseMap.get("fraud_status");
            updateOrderStatusBasedOnMidtransStatus(order, midtransStatus, fraudStatus);
        } else {
            // For manual payments, set the initial status
            order.setStatus(OrderStatus.PENDING_PAYMENT);
        }

        Payment payment = paymentService.createPayment(order, paymentType);

        order = orderRepository.save(order);
        OrderResponseDto orderResponseDto = new OrderResponseDto().mapToDto(order);

        return new OrderWithMidtransResponseDto(orderResponseDto, midtransResponseMap != null ? midtransResponseMap : new HashMap<>());
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
                inventoryService.deleteStock(order);
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

        // update payment status
        Payment payment = paymentService.getPayment(order.getMidtransTransactionId());
        PaymentStatus newPaymentStatus = MapperHelperPayment.mapOrderStatusToPaymentStatus(order.getStatus());
        paymentService.updatePaymentStatus(order.getMidtransTransactionId(), newPaymentStatus);
        return new OrderResponseDto().mapToDto(order);
    }

    @Override
    public OrderWithMidtransResponseDto getOrderStatus(String orderCode) throws MidtransError {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new DataNotFoundException("Order not found for code: " + orderCode));

        if (order.getMidtransTransactionId() == null) {
            return new OrderWithMidtransResponseDto(
                    new OrderResponseDto().mapToDto(order),
                    null
            );
        }

        JSONObject midtransResponseStatus = midtransService.getTransactionStatus(orderCode);

        return new OrderWithMidtransResponseDto(
                new OrderResponseDto().mapToDto(order),
                MapperHelper.jsonObjectToMap(midtransResponseStatus)
        );

    }

    @Override
    public OrderResponseDto markOrderAsShipped(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new DataNotFoundException("Order is not found"));

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        return new OrderResponseDto().mapToDto(order);
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

    @Override
    @Transactional
    public void updateProcessingOrdersToDelivered() {
        List<Order> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);
        Instant now = Instant.now();

        for (Order order : processingOrders) {
            Duration processingTime = Duration.between(order.getUpdatedAt(), now);
            long processingMinutes = processingTime.toMinutes();

            // Random time process order to delivered
            long randomDeliveryTime = ThreadLocalRandom.current().nextLong(30, 121);

            if (processingMinutes >= randomDeliveryTime) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                log.info("Updated order {} from processing to delivered", order.getId());
            }
        }
    }

    @Override
    @Transactional
    public OrderResponseDto confirmOrderDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found for id: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Order is not in DELIVERED status");
        }

        order.setStatus(OrderStatus.SHIPPED);
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDto().mapToDto(updatedOrder);
    }

    @Override
    public void updateDeliveredOrdersToCompleted() {
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);
        Instant now = Instant.now();
        Duration sevenDays = Duration.ofDays(7);

        for (Order order : deliveredOrders) {
            Duration timeSinceDelivery = Duration.between(order.getUpdatedAt(), now);

            if (timeSinceDelivery.compareTo(sevenDays) > 0) {
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
            }
        }
    }
}
