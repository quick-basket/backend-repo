package com.grocery.quickbasket.vouchers.dto;

import com.grocery.quickbasket.vouchers.entity.DiscountTypes;
import com.grocery.quickbasket.vouchers.entity.VoucherType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class VoucherRequestDto {

    private String code;
    private VoucherType voucherType;
    private DiscountTypes discountType;
    private BigDecimal discountValue;
    private BigDecimal minPurchase;
    private Instant startDate;
    private Instant endDate;
    private Long productId;
}