package com.grocery.quickbasket.products.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grocery.quickbasket.products.dto.ProductListResponseDto;
import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createProduct (ProductRequestDto productRequestDto);
    String updateProduct (Long id, ProductRequestDto productRequestDto);
    ProductResponseDto getProductById (Long id);
    Page<ProductListResponseDto> getAllProductsByStoreId(Long storeId, String name, String categoryName, Pageable pageable);
    Page<ProductListResponseDto> getProductsNotInInventory(Long storeId, Pageable pageable);
    Page<ProductListResponseDto> getAllProducts(Pageable pageable);
    void deleteProduct(Long id);
}
