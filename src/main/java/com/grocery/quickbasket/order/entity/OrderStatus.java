package com.grocery.quickbasket.order.entity;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_CONFIRMATION,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELED;

    public boolean canBeCancelled() {
        return this == PENDING_PAYMENT || this == PAYMENT_CONFIRMATION || this == PROCESSING;
    }
}
