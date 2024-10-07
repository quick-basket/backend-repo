package com.grocery.quickbasket.payments.service;

import java.util.List;

import com.grocery.quickbasket.payments.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payments.dto.PaymentRequestDto;
import com.grocery.quickbasket.payments.entity.Payment;

public interface PaymentService {

    PaymentListResponseDto updatePayment(Long id, PaymentRequestDto requestDto);
    List<PaymentListResponseDto> getAllPaymentListByStoreId(Long storeId);
    List<PaymentListResponseDto> getAllPayment();
}
