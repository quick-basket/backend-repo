package com.grocery.quickbasket.carts.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartSummaryResponseDto {
    private List<CartListSummaryResponseDto> carts;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalDiscountPrice;

    public CartSummaryResponseDto(List<CartListSummaryResponseDto> carts, BigDecimal totalPrice, BigDecimal totalDiscount, BigDecimal totalDiscountPrice) {
        this.carts = carts;
        this.totalPrice = totalPrice;
        this.totalDiscount = totalDiscount;
        this.totalDiscountPrice = totalDiscountPrice;
}
}
