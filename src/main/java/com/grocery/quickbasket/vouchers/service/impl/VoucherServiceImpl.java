package com.grocery.quickbasket.vouchers.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;
import com.grocery.quickbasket.vouchers.dto.UserVoucherResponseDto;
import com.grocery.quickbasket.vouchers.dto.VoucherRequestDto;
import com.grocery.quickbasket.vouchers.dto.VoucherResponseDto;
import com.grocery.quickbasket.vouchers.entity.UserVoucher;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.repository.UserVoucherRepository;
import com.grocery.quickbasket.vouchers.repository.VoucherRepository;
import com.grocery.quickbasket.vouchers.service.VoucherService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService{

    private final VoucherRepository voucherRepository;
    private final ProductRepository productRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final UserRepository userRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository, ProductRepository productRepository, UserVoucherRepository userVoucherRepository, UserRepository userRepository) {
        this.voucherRepository = voucherRepository;
        this.productRepository = productRepository;
        this.userVoucherRepository = userVoucherRepository;
        this.userRepository = userRepository;
    }

    @Override
    public VoucherResponseDto createVoucher(VoucherRequestDto voucherDTO) {
        Voucher voucher = new Voucher();
        
        if (voucherDTO.getProductId() != null) {
            Product product = productRepository.findById(voucherDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found!"));
            voucher.setProduct(product);
        } else {
            voucher.setProduct(null);
        }
        
        voucher.setCode(voucherDTO.getCode());
        voucher.setVoucherType(voucherDTO.getVoucherType());
        voucher.setDiscountType(voucherDTO.getDiscountType());
        voucher.setDiscountValue(voucherDTO.getDiscountValue());
        voucher.setMinPurchase(voucherDTO.getMinPurchase());
        voucher.setStartDate(voucherDTO.getStartDate());
        voucher.setEndDate(voucherDTO.getEndDate());
        
        Voucher savedVoucher = voucherRepository.save(voucher);
        return VoucherResponseDto.mapToDto(savedVoucher);
    }

    @Override
    public VoucherResponseDto updateVoucher(Long id, VoucherRequestDto voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("category not found!"));
        if (voucherDTO.getProductId() != null) {
            Product product = productRepository.findById(voucherDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found!"));
            existingVoucher.setProduct(product);
        } else {
            existingVoucher.setProduct(null);
        }
        existingVoucher.setCode(voucherDTO.getCode());
        existingVoucher.setVoucherType(voucherDTO.getVoucherType());
        existingVoucher.setDiscountType(voucherDTO.getDiscountType());
        existingVoucher.setDiscountValue(voucherDTO.getDiscountValue());
        existingVoucher.setMinPurchase(voucherDTO.getMinPurchase());
        existingVoucher.setStartDate(voucherDTO.getStartDate());
        existingVoucher.setEndDate(voucherDTO.getEndDate());
        Voucher updatedVoucher = voucherRepository.save(existingVoucher);

        return VoucherResponseDto.mapToDto(updatedVoucher);
    }

    @Override
    public VoucherResponseDto getVoucherById(Long id) {
        Voucher voucher = voucherRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("voucher not found!"));
        return VoucherResponseDto.mapToDto(voucher);
    }

    @Override
    public List<VoucherResponseDto> getAllVouchers() {
        List<Voucher> vouchers = voucherRepository.findAllByDeletedAtIsNull();
        return vouchers.stream()
            .map(VoucherResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteVoucher(Long id) {
        Voucher existingVoucher = voucherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new DataNotFoundException("voucher not found!"));
        List<UserVoucher> userVouchers = userVoucherRepository.findByVoucherId(id);
        for (UserVoucher userVoucher : userVouchers) {
            userVoucherRepository.delete(userVoucher); 
        }
        existingVoucher.softDelete();
        voucherRepository.save(existingVoucher);
    }

    @Override
    public List<UserVoucherResponseDto> getAllVouchersByUserId() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");
        List<UserVoucher> userVouchers = userVoucherRepository.findByUserIdAndIsUsedFalse(userId);

        return userVouchers.stream()
            .map(UserVoucherResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public void createUserVoucherIfEligible(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found with id " + userId));

        Instant now = Instant.now();
        List<Voucher> eligibleVouchers = voucherRepository.findByMinPurchaseLessThanEqualAndEndDateAfterAndDeletedAtIsNull(amount, now);

        for (Voucher voucher : eligibleVouchers) {
            boolean userVoucherExists = userVoucherRepository.existsByUserIdAndVoucherId(userId, voucher.getId());

            if (!userVoucherExists) {
                UserVoucher newUserVoucher = new UserVoucher();
                newUserVoucher.setUser(user);
                newUserVoucher.setVoucher(voucher);
                newUserVoucher.setIsUsed(false);
                newUserVoucher.setUsedAt(null);
                
                userVoucherRepository.save(newUserVoucher);
            }
        }
    }


}
