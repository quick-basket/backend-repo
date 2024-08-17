package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;

}
