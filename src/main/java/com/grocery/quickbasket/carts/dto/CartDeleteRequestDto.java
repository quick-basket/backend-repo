package com.grocery.quickbasket.carts.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDeleteRequestDto {
    private Long userId;
    private List<Long> inventoryIds;
}
