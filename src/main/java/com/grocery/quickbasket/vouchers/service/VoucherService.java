package com.grocery.quickbasket.vouchers.service;
import java.util.List;

import com.grocery.quickbasket.vouchers.dto.UserVoucherResponseDto;
import com.grocery.quickbasket.vouchers.dto.VoucherRequestDto;
import com.grocery.quickbasket.vouchers.dto.VoucherResponseDto;

public interface VoucherService {

    VoucherResponseDto createVoucher(VoucherRequestDto voucherDTO);
    VoucherResponseDto updateVoucher(Long id, VoucherRequestDto voucherDTO);
    VoucherResponseDto getVoucherById(Long id);
    List<VoucherResponseDto> getAllVouchers();
    List<UserVoucherResponseDto> getAllVouchersByUserId();
    void deleteVoucher(Long id);
}
