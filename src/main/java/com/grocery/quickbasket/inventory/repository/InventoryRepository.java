package com.grocery.quickbasket.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery.quickbasket.inventory.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductId(Long productId);
    List<Inventory> findByStoreId(Long storeId);
    Page<Inventory> findAllByStoreId(Long storeId, Pageable pageable);
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);
}
