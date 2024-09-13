package com.grocery.quickbasket.order.repository;

import com.grocery.quickbasket.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
