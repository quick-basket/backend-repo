package com.grocery.quickbasket.productImages.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.productImages.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{

}
