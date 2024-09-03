package com.grocery.quickbasket.discounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grocery.quickbasket.discounts.entity.Discount;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d " +
       "JOIN d.inventory i " +
       "JOIN i.store s " +
       "WHERE s.id = :storeId AND d.deletedAt IS NULL")
List<Discount> findAllByStoreId(@Param("storeId") Long storeId);
    Optional<Discount> findByIdAndDeletedAtIsNull(Long id);
    List<Discount> findByInventoryId(Long inventoryId);
}
