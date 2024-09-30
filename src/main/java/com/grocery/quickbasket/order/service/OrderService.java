package com.grocery.quickbasket.order.service;

import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.OrderListResponseDto;
import com.grocery.quickbasket.order.dto.OrderResponseDto;
import com.grocery.quickbasket.order.dto.OrderWithMidtransResponseDto;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {
    CheckoutDto createCheckoutSummaryFromCart();
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus);
    Order cancelOrder(Long orderId);
    List<Order> getUserOrders();
    Order getOrder(Long orderId);
//    SnapTokenResponse initiateSnapTransaction(Long orderId) throws MidtransError;
    Order createOrderFromCheckoutData(CheckoutDto checkoutData) throws MidtransError;
    List<OrderListResponseDto> getAllOrderByStoreIdAndUserId(Long storeId);
    @Transactional
    OrderWithMidtransResponseDto createOrRetrievePendingOrder(CheckoutDto checkoutData, String paymentType) throws MidtransError;
    OrderResponseDto updateOrderStatusAfterPayment(String orderId, String paymentStatus) throws MidtransError;
    OrderResponseDto getOrderStatus(Long orderId) throws MidtransError;
    BigDecimal getTotalAmountAllStore();
    BigDecimal getTotalAmountFromOrdersLastWeek();
    BigDecimal getTotalAmountFromOrdersLastMonth();
    BigDecimal getTotalAmountByStoreAndCategory(Long storeId, Long categoryId);
    BigDecimal getTotalAmountByStoreAndProduct(Long storeId, Long productId);
    BigDecimal getTotalAmountByStoreId(Long storeId);
}
