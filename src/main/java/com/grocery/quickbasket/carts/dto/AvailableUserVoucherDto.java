package com.grocery.quickbasket.carts.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.grocery.quickbasket.vouchers.entity.DiscountTypes;
import com.grocery.quickbasket.vouchers.entity.VoucherType;

import lombok.Data;

@Data
public class AvailableUserVoucherDto {
    private Long userVoucherId;  
    private Long voucherId;     
    private String voucherCode;
    private BigDecimal discountValue;
    private DiscountTypes discountType;
    private VoucherType voucherType;
    private BigDecimal minPurchase;
    private Instant startDate;
    private Instant endDate;
    private Boolean isUsed;   
    
}
