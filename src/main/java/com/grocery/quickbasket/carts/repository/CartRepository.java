package com.grocery.quickbasket.carts.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.carts.entity.Cart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUserId(Long userId);
    List<Cart> findAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.inventory.id IN :inventoryIds")
    void deleteByUserIdAndInventoryIdIn(@Param("userId") Long userId, @Param("inventoryIds") List<Long> inventoryIds);
    void deleteAllByUserIdAndInventoryStoreId(Long userId, Long storeId);
    Optional<Cart> findByUserIdAndInventoryId(Long userId, Long inventoryId);
}
