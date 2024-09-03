package com.grocery.quickbasket.productCategory.controller;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import com.grocery.quickbasket.productCategory.service.ProductCategoryService;
import com.grocery.quickbasket.response.Response;

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
    public ResponseEntity<?> createProductCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory createdCategory = productCategoryService.createProductCategory(productCategory);
        return Response.successResponse("product category created", createdCategory);
    }

    @GetMapping
    public ResponseEntity<?> getAllProductCategories() {
        List<ProductCategory> categories = productCategoryService.getAllProductCategory();
        return Response.successResponse("fetch all product categories", categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductCategoryById(@PathVariable Long id) {
        Optional<ProductCategory> category = productCategoryService.getProductCategoryById(id);
        return Response.successResponse("fetch product category", category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductCategory(@PathVariable Long id, @RequestBody ProductCategory productCategory) {
            ProductCategory updatedCategory = productCategoryService.updateProductCategory(id, productCategory);
            return Response.successResponse("successfully udpated product category", updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductCategory(@PathVariable Long id) {
        try {
            productCategoryService.deleteProductCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataNotFoundException e ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
