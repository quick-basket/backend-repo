package com.grocery.quickbasket.discounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.discounts.entity.Discount;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByStoreId(Long storeId);
}
