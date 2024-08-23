package com.grocery.quickbasket.productImages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery.quickbasket.productImages.entity.ProductImage;
import com.grocery.quickbasket.products.entity.Product;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
}
