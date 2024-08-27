package com.grocery.quickbasket.productCategory.repository;

import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    boolean existsByName(String name);
}
