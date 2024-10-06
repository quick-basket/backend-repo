package com.grocery.quickbasket.order.service;

import com.grocery.quickbasket.order.dto.*;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    CheckoutDto createCheckoutSummaryFromCart(Long storeId);
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus);
    Order cancelOrder(Long orderId);
    OrderListDetailDto getUserOrders(int page, int size);
    Order getOrder(Long orderId);
    OrderWithMidtransResponseDto getPendingOrder(Long userId);
    Order createOrderFromCheckoutData(CheckoutDto checkoutData) throws MidtransError;
    List<OrderListResponseDto> getAllOrderByStoreIdAndUserId(Long storeId);
    @Transactional
    OrderWithMidtransResponseDto createOrder(CheckoutDto checkoutData, String paymentType) throws MidtransError;
    OrderResponseDto updateOrderStatusAfterPayment(String orderId, String paymentStatus) throws MidtransError;
    OrderWithMidtransResponseDto getOrderStatus(String orderCode) throws MidtransError;
    BigDecimal getTotalAmountAllStore();
    BigDecimal getTotalAmountFromOrdersLastWeek();
    BigDecimal getTotalAmountFromOrdersLastMonth();
    BigDecimal getTotalAmountByStoreAndCategory(Long storeId, Long categoryId);
    BigDecimal getTotalAmountByStoreAndProduct(Long storeId, Long productId);
    BigDecimal getTotalAmountByStoreId(Long storeId);
}
