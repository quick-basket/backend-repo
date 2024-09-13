package com.grocery.quickbasket.carts.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

import com.grocery.quickbasket.carts.entity.Cart;

@Data
public class CartListResponseDto {
    private Long id;
    private Long userId;
    private Long inventoryId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int quantity;
    private List<String> imageUrls;

    public static CartListResponseDto mapToDto(Cart cart, List<String> imageUrls) {
        CartListResponseDto dto = new CartListResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setInventoryId(cart.getInventory().getId());
        dto.setProductId(cart.getInventory().getProduct().getId());
        dto.setProductName(cart.getInventory().getProduct().getName());
        dto.setPrice(cart.getPrice());
        dto.setDiscountPrice(cart.getDiscountPrice());
        dto.setQuantity(cart.getQuantity());
        dto.setImageUrls(imageUrls);
        return dto;
    }
    
}
