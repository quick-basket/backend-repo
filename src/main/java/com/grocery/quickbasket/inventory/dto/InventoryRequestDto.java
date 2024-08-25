package com.grocery.quickbasket.inventory.dto;

import lombok.Data;

@Data
public class InventoryRequestDto {

    private Long ProductId;
    private Long storeId;
    private Integer quantity;
}
