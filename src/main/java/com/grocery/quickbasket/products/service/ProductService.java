package com.grocery.quickbasket.products.service;

import java.util.List;

import com.grocery.quickbasket.products.dto.ProductListResponseDto;
import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createProduct (ProductRequestDto productRequestDto);
    String updateProduct (Long id, ProductRequestDto productRequestDto);
    ProductResponseDto getProductById (Long id);
    List<ProductListResponseDto> getAllProducts(Long storeId);
    void deleteProduct(Long id);
}
