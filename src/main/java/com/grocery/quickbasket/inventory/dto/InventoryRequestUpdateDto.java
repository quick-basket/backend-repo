package com.grocery.quickbasket.inventory.dto;

import lombok.Data;

@Data
public class InventoryRequestUpdateDto {

    private Long productId;
    private Long storeId;
    private int quantity;
}
