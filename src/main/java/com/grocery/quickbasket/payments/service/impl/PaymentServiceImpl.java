package com.grocery.quickbasket.payments.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.payments.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payments.dto.PaymentRequestDto;
import com.grocery.quickbasket.payments.entity.Payment;
import com.grocery.quickbasket.payments.repository.PaymentRepository;
import com.grocery.quickbasket.payments.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl (PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentListResponseDto updatePayment(Long id, PaymentRequestDto requestDto) {

        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("payment id not found with id " + id));
        payment.setPaymentStatus(requestDto.getPaymentStatus());

        Order order = payment.getOrder();

        if ("PAID".equals(requestDto.getPaymentStatus())) {
            order.setStatus(OrderStatus.PROCESSING); 
        } else if ("PENDING".equals(requestDto.getPaymentStatus())) {
            order.setStatus(OrderStatus.PENDING_PAYMENT); 
        } else if ("CANCELED".equals(requestDto.getPaymentStatus())) {
            order.setStatus(OrderStatus.CANCELED); 
        }
        orderRepository.save(order);

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentListResponseDto.mapToDto(updatedPayment);
    }

    @Override
    public List<PaymentListResponseDto> getAllPaymentListByStoreId(Long storeid) {
        List<Payment> payments = paymentRepository.findByOrderStoreId(storeid);

        return payments.stream()
            .map(PaymentListResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

}
