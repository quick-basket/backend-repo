package com.grocery.quickbasket.carts.dto;

import lombok.Data;

@Data
public class CartRequestDto {
    private Long inventoryId;
    private int quantity;
}
