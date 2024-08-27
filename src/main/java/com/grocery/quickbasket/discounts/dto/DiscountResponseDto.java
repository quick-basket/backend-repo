package com.grocery.quickbasket.discounts.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.grocery.quickbasket.discounts.entity.Discount;

import lombok.Data;

@Data
public class DiscountResponseDto {
    private Long id;
    private String storeName;
    private String productName;
    private String type;
    private BigDecimal value;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Instant startDate;
    private Instant endDate;

    public static DiscountResponseDto formDiscount (Discount discount) {
        DiscountResponseDto dto = new DiscountResponseDto();
        dto.setId(discount.getId());
        dto.setStoreName(discount.getStore().getName());
        dto.setProductName(discount.getProduct().getName());
        dto.setType(discount.getType().name());
        dto.setValue(discount.getValue());
        dto.setMinPurchase(discount.getMinPurchase());
        dto.setMaxDiscount(discount.getMaxDiscount());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        return dto;
    }

}
