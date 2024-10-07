package com.grocery.quickbasket.payment.service;

import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.payment.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payment.dto.PaymentRequestDto;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Order order, String paymentMethod);
    void updatePaymentStatus(String transactionId, PaymentStatus paymentStatus);
    Payment getPayment(String transactionId);
    Payment confirmPayment(String transactionId);
    Payment uploadPaymentProof(String transactionId, MultipartFile file);

    // service dio
    //TODO
    //Check before merging to dev
    PaymentListResponseDto updatePayment(Long id, PaymentRequestDto requestDto);
    List<PaymentListResponseDto> getAllPaymentListByStoreId(Long storeId);
}
