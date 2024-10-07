package com.grocery.quickbasket.carts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.carts.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUserId(Long userId);
    List<Cart> findAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
    void deleteAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
    Optional<Cart> findByUserIdAndInventoryId(Long userId, Long inventoryId);
}
