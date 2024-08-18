package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
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
    private Long storeId;
    private String storeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
