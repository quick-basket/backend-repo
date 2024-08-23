package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ProductListResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private List<String> imageUrls;
    private List<Long> imageIds;

}
