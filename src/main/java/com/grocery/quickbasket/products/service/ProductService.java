package com.grocery.quickbasket.products.service;

import java.util.List;

import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createProduct (ProductRequestDto productRequestDto);
    ProductResponseDto updateProduct (Long id, ProductRequestDto productRequestDto);
    ProductResponseDto getProductById (Long id);
    List<ProductResponseDto> getAllProducts();
    void deleteProduct(Long id);
}
