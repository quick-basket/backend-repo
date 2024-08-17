package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
