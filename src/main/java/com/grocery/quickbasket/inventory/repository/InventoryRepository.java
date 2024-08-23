package com.grocery.quickbasket.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery.quickbasket.inventory.entity.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
}
