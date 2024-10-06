package com.grocery.quickbasket.payment.service.Impl;

import com.grocery.quickbasket.cloudinary.service.CloudinaryService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import com.grocery.quickbasket.payment.repository.PaymentRepository;
import com.grocery.quickbasket.payment.service.PaymentService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final CloudinaryService cloudinaryService;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, @Lazy OrderService orderService, CloudinaryService cloudinaryService, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.cloudinaryService = cloudinaryService;
        this.orderRepository = orderRepository;
    }

    @Override
    public Payment createPayment(Order order, String paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getTotalAmount());

        if (order.getMidtransTransactionId() == null) {
            payment.setTransactionId(order.getOrderCode());
        } else {
            payment.setTransactionId(order.getMidtransTransactionId());
        }

        payment.setPaymentStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(String transactionId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        payment.setPaymentStatus(paymentStatus);
        paymentRepository.save(payment);
    }

    @Override
    public Payment getPayment(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));
    }

    @Override
    public Payment confirmPayment(String transactionId) {
        Payment payment = getPayment(transactionId);

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        Payment updatedPayment = paymentRepository.save(payment);

        orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.PROCESSING);

        return updatedPayment;

    }

    @Override
    public Payment uploadPaymentProof(String orderCode, MultipartFile file) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        try {
            String imageUrl = cloudinaryService.uploadProfileUserImage(file);
            payment.setPaymentProofUrl(imageUrl);
            payment.setPaymentStatus(PaymentStatus.PAYMENT_CONFIRMATION);
            order.setStatus(OrderStatus.PAYMENT_CONFIRMATION);
            return paymentRepository.save(payment);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to upload payment proof", e);
        }
    }
}
