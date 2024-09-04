package com.grocery.quickbasket.discounts.dto;

import java.math.BigDecimal;

import com.grocery.quickbasket.discounts.entity.DiscountType;

import lombok.Data;

@Data
public class DiscountProductListDto {

    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountPrice;  
}
