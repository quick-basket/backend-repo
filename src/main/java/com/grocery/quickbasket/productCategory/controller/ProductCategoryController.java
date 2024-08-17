package com.grocery.quickbasket.productCategory.controller;

import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import com.grocery.quickbasket.productCategory.service.ProductCategoryService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping()
    public ResponseEntity<ProductCategory> createProductCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory createdCategory = productCategoryService.createProductCategory(productCategory);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllProductCategories() {
        List<ProductCategory> categories = productCategoryService.getAllProductCategory();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getProductCategoryById(@PathVariable Long id) {
        Optional<ProductCategory> category = productCategoryService.getProductCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> updateProductCategory(@PathVariable Long id, ProductCategory productCategory) {
        try {
            ProductCategory updatedCategory = productCategoryService.updateProductCategory(id, productCategory);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductCategory(@PathVariable Long id) {
        try {
            productCategoryService.deleteProductCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
