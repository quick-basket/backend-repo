package com.grocery.quickbasket.productCategory.service.impl;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import com.grocery.quickbasket.productCategory.repository.ProductCategoryRepository;
import com.grocery.quickbasket.productCategory.service.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }
    @Override
    public ProductCategory createProductCategory(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    @Override
    public ProductCategory updateProductCategory(Long id, ProductCategory productCategory) {
        Optional<ProductCategory> existingCategory = productCategoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            ProductCategory categoryUpdate = existingCategory.get();
            categoryUpdate.setName(productCategory.getName());
            return productCategoryRepository.save(categoryUpdate);
        } else {
            throw new DataNotFoundException("product category not found with id " + id);
        }
    }

    @Override
    public Optional<ProductCategory> getProductCategoryById(Long id) {
        return productCategoryRepository.findById(id);
    }

    @Override
    public List<ProductCategory> getAllProductCategory() {
        return productCategoryRepository.findAll();
    }

    @Override
    public void deleteProductCategory(Long id) {
        if (productCategoryRepository.existsById(id)) {
            productCategoryRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("product category not found with id " + id);
        }
    }
}
