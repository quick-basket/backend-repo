package com.grocery.quickbasket.inventory.dto;

import java.time.LocalDateTime;

import com.grocery.quickbasket.inventory.entity.Inventory;

import lombok.Data;

@Data
public class InventoryResponseDto {

    private Long id;
    private String productName;
    private String storeName;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryResponseDto mapToDto (Inventory inventory) {
        InventoryResponseDto dto = new InventoryResponseDto();
        dto.setId(inventory.getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setStoreName(inventory.getStore().getName());
        dto.setCreatedAt(inventory.getCreatedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        return dto;
    }
}
