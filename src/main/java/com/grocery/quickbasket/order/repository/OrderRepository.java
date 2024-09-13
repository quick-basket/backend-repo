package com.grocery.quickbasket.order.repository;

import com.grocery.quickbasket.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
