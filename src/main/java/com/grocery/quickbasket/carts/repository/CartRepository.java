package com.grocery.quickbasket.carts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.carts.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUserId(Long userId);
    List<Cart> findAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
    void deleteAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
}
