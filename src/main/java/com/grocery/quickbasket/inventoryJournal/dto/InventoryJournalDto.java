package com.grocery.quickbasket.inventoryJournal.dto;

import java.time.Instant;

import com.grocery.quickbasket.inventoryJournal.entity.InventoryJournal;

import lombok.Data;

@Data
public class InventoryJournalDto {

    private Long id;
    private Long inventoryId;
    private Long productId;
    // private Long userId;
    // private String userName;
    private String productName;
    private Long storeId;
    private String storeName;
    private Integer quantityChange;
    private Instant createdAt;

    public static InventoryJournalDto mapToDto(InventoryJournal inventoryJournal) {
        InventoryJournalDto dto = new InventoryJournalDto();
        dto.setId(inventoryJournal.getId());
        dto.setInventoryId(inventoryJournal.getInventory().getId());
        dto.setProductId(inventoryJournal.getInventory().getProduct().getId());
        // dto.setUserId(inventoryJournal.getUser().getId());
        // dto.setUserName(inventoryJournal.getUser().getName());
        dto.setProductName(inventoryJournal.getInventory().getProduct().getName());
        dto.setStoreId(inventoryJournal.getInventory().getStore().getId());
        dto.setStoreName(inventoryJournal.getInventory().getStore().getName());
        dto.setQuantityChange(inventoryJournal.getQuantityChange());
        dto.setCreatedAt(inventoryJournal.getCreatedAt());
        return dto;
    }
}
