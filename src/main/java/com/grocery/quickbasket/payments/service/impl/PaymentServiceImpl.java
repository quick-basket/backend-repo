package com.grocery.quickbasket.payments.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.inventoryJournal.entity.InventoryJournal;
import com.grocery.quickbasket.inventoryJournal.repository.InventoryJournalRepository;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderItem;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderItemRepository;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.payments.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payments.dto.PaymentRequestDto;
import com.grocery.quickbasket.payments.entity.Payment;
import com.grocery.quickbasket.payments.repository.PaymentRepository;
import com.grocery.quickbasket.payments.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private InventoryJournalRepository inventoryJournalRepository;

    public PaymentServiceImpl (PaymentRepository paymentRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, InventoryRepository inventoryRepository, InventoryJournalRepository inventoryJournalRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryJournalRepository = inventoryJournalRepository;
    }

    @Override
    @Transactional

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
            updateInventory(order);
        } else if ("PENDING".equals(requestDto.getPaymentStatus())) {
            order.setStatus(OrderStatus.PENDING_PAYMENT); 
        } else if ("CANCELED".equals(requestDto.getPaymentStatus())) {
            order.setStatus(OrderStatus.CANCELED); 
        }
        orderRepository.save(order);

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentListResponseDto.mapToDto(updatedPayment);
    }

    private void updateInventory (Order order) {
        Long storeId = order.getStore().getId();
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProduct().getId();
            int orderQuantity = orderItem.getQuantity();

            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new DataNotFoundException("Inventory not found for productId: " + productId + " and storeId: " + storeId));

            int currentQuantity = inventory.getQuantity();
            if (currentQuantity < orderQuantity) {
                throw new DataNotFoundException("Insufficient inventory for productId: " + productId);
            }
            int newQuantity = currentQuantity - orderQuantity;
            inventory.setQuantity(newQuantity);

            InventoryJournal journal = new InventoryJournal();
            journal.setInventory(inventory);
            journal.setQuantityChange(-orderQuantity);
            
            inventory.getJournals().add(journal);
            inventoryRepository.save(inventory);
            
        }
    }
    

    @Override
    public List<PaymentListResponseDto> getAllPaymentListByStoreId(Long storeId) {
        List<Payment> payments = paymentRepository.findByOrderStoreId(storeId);

        return payments.stream()
            .map(PaymentListResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaymentListResponseDto> getAllPayment() {

    @Override
    public List<PaymentListResponseDto> getAllPaymentListByStoreId(Long storeid) {
        List<Payment> payments = paymentRepository.findByOrderStoreId(storeid);

        return payments.stream()
            .map(PaymentListResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

}
