package com.grocery.quickbasket.inventory.dto;

import com.grocery.quickbasket.inventory.entity.Inventory;

import lombok.Data;

@Data
public class InventoryListResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productCategory;
    private Long storeId;
    private String storeName;
    private Integer quantity;

    public static InventoryListResponseDto mapToDto(Inventory inventory) {
        InventoryListResponseDto dto = new InventoryListResponseDto();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProduct().getId());
        dto.setProductName(inventory.getProduct().getName());
        dto.setProductCategory(inventory.getProduct().getCategory().getName());
        dto.setStoreId(inventory.getStore().getId());
        dto.setStoreName(inventory.getStore().getName());
        dto.setQuantity(inventory.getQuantity());
        return dto;
    }
}
