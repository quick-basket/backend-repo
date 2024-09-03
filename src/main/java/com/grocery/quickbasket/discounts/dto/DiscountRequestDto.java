package com.grocery.quickbasket.discounts.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.grocery.quickbasket.discounts.entity.DiscountType;

import lombok.Data;

@Data
public class DiscountRequestDto {

    private Long inventoryId;
    private String type;  
    private BigDecimal value;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Instant startDate;
    private Instant endDate;

    public DiscountType getTypeAsEnum() {
        return DiscountType.valueOf(type.toUpperCase());
    }
}
