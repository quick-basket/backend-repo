package com.grocery.quickbasket.discounts.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DiscountProductListDto {

    private Long id;
    private BigDecimal discountValue;
    private BigDecimal currentPrice;  
}
