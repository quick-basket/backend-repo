package com.grocery.quickbasket.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.grocery.quickbasket.discounts.dto.DiscountProductListDto;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.products.entity.Product;

import lombok.Data;

@Data
public class InventoryDetailResponseDto {
    private Long id;
    private Long inventoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private List<Long> imageIds;
    private Integer quantity;
    private DiscountProductListDto discount;
    private Instant createdAt;
    private Instant updatedAt;

    public static InventoryDetailResponseDto mapToDto (Product product, Inventory inventory ) {
        InventoryDetailResponseDto dto = new InventoryDetailResponseDto();
        dto.setId(product.getId());
        dto.setInventoryId(inventory.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setQuantity(inventory.getQuantity());
        return dto;
    }
}
