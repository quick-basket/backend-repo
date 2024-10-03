package com.grocery.quickbasket.payments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.payments.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderStoreId(Long storeId);
}
