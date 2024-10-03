package com.grocery.quickbasket.payment.dto;

import com.grocery.quickbasket.payment.entity.Payment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PaymentDto {
    private String transactionId;
    private String paymentProofUrl;

    public static PaymentDto fromEntity(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        BeanUtils.copyProperties(payment, paymentDto);
        return paymentDto;
    }
}
