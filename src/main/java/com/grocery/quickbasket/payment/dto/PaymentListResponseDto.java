package com.grocery.quickbasket.payment.dto;

import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentListResponseDto {

    private Long id;
    private Long orderId;
    private Long storeId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentProof;
    private PaymentStatus paymentStatus;

    public static PaymentListResponseDto mapToDto(Payment payment) {
        PaymentListResponseDto dto = new PaymentListResponseDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setStoreId(payment.getOrder().getStore().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentProof(payment.getPaymentProofUrl());
        dto.setPaymentStatus(payment.getPaymentStatus());
        return dto;
    }
}
