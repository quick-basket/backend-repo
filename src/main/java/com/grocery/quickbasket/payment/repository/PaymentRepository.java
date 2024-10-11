package com.grocery.quickbasket.payment.repository;

import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionIdOrOrder_OrderCode(String transactionId, String order_orderCode);

    List<Payment> findByOrderStoreIdAndPaymentProofUrlIsNotNull(Long storeId);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = :status " +
            "AND p.paymentMethod = :method " +
            "AND p.updatedAt < :expiryTime")
    List<Payment> findExpiredManualPayments(
            @Param("status") PaymentStatus status,
            @Param("method") String method,
            @Param("expiryTime") Instant expiryTime
    );

}
