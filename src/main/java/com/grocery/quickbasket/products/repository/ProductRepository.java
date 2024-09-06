package com.grocery.quickbasket.products.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.products.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);
    
}
