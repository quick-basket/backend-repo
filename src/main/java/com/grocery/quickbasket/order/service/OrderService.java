package com.grocery.quickbasket.order.service;

import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.OrderListResponseDto;
import com.grocery.quickbasket.order.dto.OrderResponseDto;
import com.grocery.quickbasket.order.dto.SnapTokenResponse;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.midtrans.httpclient.error.MidtransError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {
    CheckoutDto createCheckoutSummaryFromCart();
    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus);
    Order cancelOrder(Long orderId);
    List<Order> getUserOrders();
    Order getOrder(Long orderId);
    SnapTokenResponse initiateSnapTransaction(CheckoutDto checkoutData) throws MidtransError;
    Order createOrderFromCheckoutData(CheckoutDto checkoutData);
    Map<String, Object> buildMidtransRequest(Order order, CheckoutDto checkoutData);
    List<OrderListResponseDto> getAllOrderByStoreIdAndUserId(Long storeId);
    BigDecimal getTotalAmountAllStore();
    BigDecimal getTotalAmountFromOrdersLastWeek();
    BigDecimal getTotalAmountFromOrdersLastMonth();
    BigDecimal getTotalAmountByStoreAndCategory(Long storeId, Long categoryId);
    BigDecimal getTotalAmountByStoreAndProduct(Long storeId, Long productId);
    BigDecimal getTotalAmountByStoreId(Long storeId);
}
