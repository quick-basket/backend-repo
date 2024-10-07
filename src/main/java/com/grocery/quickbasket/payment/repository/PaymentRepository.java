package com.grocery.quickbasket.payment.repository;

import com.grocery.quickbasket.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionIdOrOrder_OrderCode(String transactionId, String order_orderCode);

    List<Payment> findByOrderStoreId(Long storeId);

}
