package com.grocery.quickbasket.discounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grocery.quickbasket.discounts.entity.Discount;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d JOIN d.inventory i WHERE i.store.id = :storeId")
    List<Discount> findAllByStoreId(@Param("storeId") Long storeId);
}
