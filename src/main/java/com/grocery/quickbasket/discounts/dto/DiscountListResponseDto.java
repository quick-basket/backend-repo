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
    private String storeName;
    private Long productId;
    private String productName;

    public static DiscountListResponseDto fromEntity(Discount discount) {
        DiscountListResponseDto dto = new DiscountListResponseDto();
        dto.setId(discount.getId());
        dto.setType(discount.getType().name());
        dto.setValue(discount.getValue());
        dto.setMinPurchase(discount.getMinPurchase());
        dto.setMaxDiscount(discount.getMaxDiscount());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        dto.setStoreId(discount.getInventory().getStore().getId());
        dto.setStoreName(discount.getInventory().getStore().getName());
        dto.setProductId(discount.getInventory().getProduct().getId());
        dto.setProductName(discount.getInventory().getProduct().getName());
        return dto;
    }
}
