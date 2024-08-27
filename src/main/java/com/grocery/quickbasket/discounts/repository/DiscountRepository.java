package com.grocery.quickbasket.discounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.discounts.entity.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

}
