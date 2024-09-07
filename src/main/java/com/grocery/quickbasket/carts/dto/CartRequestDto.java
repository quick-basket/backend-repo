package com.grocery.quickbasket.carts.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartRequestDto {
    private Long userId;
    private Long inventoryId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int quantity;
}
