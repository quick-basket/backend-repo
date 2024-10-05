package com.grocery.quickbasket.order.repository;

import com.grocery.quickbasket.order.entity.Order;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreIdAndUserId(Long storeId, Long userId);
    Optional<Order> findTopByUserIdAndStoreAndStatusOrderByCreatedAtDesc(Long userId, Store store, OrderStatus status);
    Optional<Order> findByOrderCode(String orderCode);
    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
