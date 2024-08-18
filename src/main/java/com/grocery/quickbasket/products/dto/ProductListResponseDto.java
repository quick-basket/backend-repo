package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductListResponseDto {

    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
}
