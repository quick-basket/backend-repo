package com.grocery.quickbasket.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.products.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
