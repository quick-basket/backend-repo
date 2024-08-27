package com.grocery.quickbasket.discounts.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.grocery.quickbasket.discounts.entity.Discount;

import lombok.Data;
@Data
public class DiscountListResponseDto {

    private Long id;
    private String type;
    private BigDecimal value;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Instant startDate;
    private Instant endDate;
    private Long storeId;
    private Long productId;

    public static DiscountListResponseDto fromEntity(Discount discount) {
        DiscountListResponseDto dto = new DiscountListResponseDto();
        dto.setId(discount.getId());
        dto.setType(discount.getType().name());
        dto.setValue(discount.getValue());
        dto.setMinPurchase(discount.getMinPurchase());
        dto.setMaxDiscount(discount.getMaxDiscount());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        dto.setStoreId(discount.getStore().getId());
        dto.setProductId(discount.getProduct().getId());
        return dto;
    }
}
