package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.util.List;

import com.grocery.quickbasket.discounts.dto.DiscountProductListDto;
import com.grocery.quickbasket.products.entity.Product;

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
    private DiscountProductListDto discount;

     public static ProductListResponseDto convertToDto(Product product) {
        ProductListResponseDto dto = new ProductListResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
     }
}
