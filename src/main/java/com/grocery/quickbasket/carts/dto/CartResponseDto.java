package com.grocery.quickbasket.carts.dto;

import lombok.Data;
import java.math.BigDecimal;

import com.grocery.quickbasket.carts.entity.Cart;

@Data
public class CartResponseDto {
    private Long id;
    private Long userId;
    private Long inventoryId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int quantity;

    public static CartResponseDto mapToDto (Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setInventoryId(cart.getInventory().getId());
        dto.setPrice(cart.getPrice());
        dto.setDiscountPrice(cart.getDiscountPrice());
        dto.setQuantity(cart.getQuantity());
        return dto;
    }
}
