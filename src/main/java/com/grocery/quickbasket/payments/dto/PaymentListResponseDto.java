package com.grocery.quickbasket.payments.dto;

import java.math.BigDecimal;

import com.grocery.quickbasket.payments.entity.Payment;

import lombok.Data;

@Data
public class PaymentListResponseDto {

    private Long id;
    private Long orderId;
    private Long storeId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentProof;
    private String paymentStatus;

    public static PaymentListResponseDto mapToDto(Payment payment) {
        PaymentListResponseDto dto = new PaymentListResponseDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setStoreId(payment.getOrder().getStore().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentProof(payment.getPaymentProof());
        dto.setPaymentStatus(payment.getPaymentStatus());
        return dto;
    }
}
