package com.grocery.quickbasket.order.service.Impl;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.OrderListResponseDto;
import com.grocery.quickbasket.order.dto.OrderResponseDto;
import com.grocery.quickbasket.order.dto.SnapTokenResponse;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderItem;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderItemRepository;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.products.service.ProductService;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.dto.UserAddressDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.entity.UserAddress;
import com.grocery.quickbasket.user.service.UserAddressService;
import com.grocery.quickbasket.user.service.UserService;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final UserAddressService addressService;
    private final CartService cartService;
    private final StoreService storeService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final OrderItemRepository orderItemRepository;

    // Inject Midtrans configuration
    @Value("${midtrans.server.key}")
    private String midtransServerKey;

    @Value("${midtrans.client.key}")
    private String midtransClientKey;

    public OrderServiceImpl(UserService userService, OrderRepository orderRepository, UserAddressService addressService, CartService cartService, StoreService storeService, ProductService productService, ProductRepository productRepository, StoreRepository storeRepository, OrderItemRepository orderItemRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.addressService = addressService;
        this.cartService = cartService;
        this.storeService = storeService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.orderItemRepository = orderItemRepository;
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
        List<CartListResponseDto> itemListFromCart = cartService.getAllCartByUserId();
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

//        CartSummaryResponseDto cartSummary = cartService.getCartSummary();
//        CheckoutDto.Summary summary = new CheckoutDto.Summary();
//        summary.setSubtotal(cartSummary.getTotalPrice());
//        summary.setDiscount(cartSummary.getTotalDiscount());
//        summary.setTotal(cartSummary.getTotalDiscountPrice());
//        summary.setShippingCost(BigDecimal.valueOf(5000));
//        checkoutDto.setSummary(summary);

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
        return null;
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
    public SnapTokenResponse initiateSnapTransaction(CheckoutDto checkoutData) throws MidtransError {
        //Set Midtrans configuration
        Midtrans.serverKey = midtransServerKey;
        Midtrans.clientKey = midtransClientKey;

        //Create order
        Order order = createOrderFromCheckoutData(checkoutData);
        orderRepository.save(order);

        //Build midtrans request
        Map<String, Object> params = buildMidtransRequest(order, checkoutData);

        //Call Midtrans Api to create transaction token
        String snapToken = SnapApi.createTransactionToken(params);

        return new SnapTokenResponse(snapToken, order.getId(), Midtrans.getClientKey());
    }

    @Override
    public Order createOrderFromCheckoutData(CheckoutDto checkoutData) {
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
                    orderItem.setPrice(item.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setItems(orderItems);

        return order;
    }

    @Override
    public Map<String, Object> buildMidtransRequest(Order order, CheckoutDto checkoutData) {
        Map<String, Object> params = new HashMap<>();
    
        BigDecimal totalAmount = order.getTotalAmount();
    
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
    
        // Menambahkan shipping cost sebagai item terpisah
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
    
        return params;
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
}
