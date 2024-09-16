package com.grocery.quickbasket.carts.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryResponseDto {
    private List<CartListSummaryResponseDto> cartList;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalDiscountPrice;
    // private List<AvailableUserVoucherDto> availableVouchers;

}
