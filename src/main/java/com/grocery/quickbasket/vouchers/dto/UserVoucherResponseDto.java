package com.grocery.quickbasket.vouchers.dto;

import java.time.Instant;

import com.grocery.quickbasket.vouchers.entity.UserVoucher;

import com.grocery.quickbasket.vouchers.repository.VoucherRepository;
import lombok.Data;

@Data
public class UserVoucherResponseDto {
    private Long id;
    private Long userId;
    private Long voucherId;
    private String name;
    private Boolean isUsed;
    private Instant usedAt;

    public static UserVoucherResponseDto mapToDto(UserVoucher userVoucher) {
        UserVoucherResponseDto responseDto = new UserVoucherResponseDto();
        responseDto.setId(userVoucher.getId());
        responseDto.setUserId(userVoucher.getUser().getId());
        responseDto.setVoucherId(userVoucher.getVoucher().getId());
        responseDto.setName(userVoucher.getVoucher().getCode());
        responseDto.setIsUsed(userVoucher.getIsUsed());
        responseDto.setUsedAt(userVoucher.getUsedAt());
        return responseDto;
    }

}
