package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

import com.grocery.quickbasket.products.entity.Product;

import lombok.Data;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponseDto mapToDto (Product product ) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}

    

