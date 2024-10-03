package com.grocery.quickbasket.payment.mapper;

import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.payment.entity.PaymentStatus;

public class MapperHelperPayment {
    public static PaymentStatus mapMidtransStatusToPaymentStatus(String midtransStatus) {
        switch (midtransStatus.toLowerCase()) {
            case "capture":
            case "settlement":
                return PaymentStatus.COMPLETED;
            case "pending":
                return PaymentStatus.PENDING;
            default:
                return PaymentStatus.FAILED;
        }
    }

    public static PaymentStatus mapOrderStatusToPaymentStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case PROCESSING:
            case SHIPPED:
            case DELIVERED:
                return PaymentStatus.COMPLETED;
            case PENDING_PAYMENT:
            case PAYMENT_CONFIRMATION:
                return PaymentStatus.PENDING;
            case CANCELED:
                return PaymentStatus.FAILED;
            default:
                return PaymentStatus.PENDING;
        }
    }
}
