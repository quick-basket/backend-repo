package com.grocery.quickbasket.productCategory.service;

import com.grocery.quickbasket.productCategory.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryService {
    ProductCategory createProductCategory(ProductCategory productCategory);
    ProductCategory updateProductCategory(Long id, ProductCategory productCategory);
    Optional<ProductCategory> getProductCategoryById(Long id);
    List<ProductCategory> getAllProductCategory();
    void deleteProductCategory(Long id);
}
