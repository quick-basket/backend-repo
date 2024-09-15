package com.grocery.quickbasket.vouchers.dto;

import com.grocery.quickbasket.vouchers.entity.DiscountTypes;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.entity.VoucherType;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class VoucherResponseDto {

    private Long id;
    private String code;
    private VoucherType voucherType;
    private DiscountTypes discountType;
    private BigDecimal discountValue;
    private BigDecimal minPurchase;
    private Instant startDate;
    private Instant endDate;
    private Long productId;
    private String productName;
    private Instant createdAt;
    private Instant updatedAt;

    public static VoucherResponseDto mapToDto(Voucher voucher) {
        VoucherResponseDto dto = new VoucherResponseDto();
        dto.setId(voucher.getId());
        dto.setCode(voucher.getCode());
        dto.setVoucherType(voucher.getVoucherType());
        dto.setDiscountType(voucher.getDiscountType());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setMinPurchase(voucher.getMinPurchase());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        if (voucher.getProduct() != null) {
            dto.setProductId(voucher.getProduct().getId());
            dto.setProductName(voucher.getProduct().getName());
        }
        dto.setCreatedAt(voucher.getCreatedAt());
        dto.setUpdatedAt(voucher.getUpdatedAt());
        return dto;
    }
}
