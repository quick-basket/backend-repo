package com.grocery.quickbasket.payment.service;

import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    Payment createPayment(Order order, String paymentMethod);
    void updatePaymentStatus(String transactionId, PaymentStatus paymentStatus);
    Payment getPayment(String transactionId);
    Payment confirmPayment(String transactionId);
    Payment uploadPaymentProof(String transactionId, MultipartFile file);
}
