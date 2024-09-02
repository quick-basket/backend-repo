package com.grocery.quickbasket.vouchers.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.vouchers.dto.VoucherRequestDto;
import com.grocery.quickbasket.vouchers.dto.VoucherResponseDto;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.repository.VoucherRepository;
import com.grocery.quickbasket.vouchers.service.VoucherService;

@Service
public class VoucherServiceImpl implements VoucherService{

    private final VoucherRepository voucherRepository;
    private final ProductRepository productRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository, ProductRepository productRepository) {
        this.voucherRepository = voucherRepository;
        this.productRepository = productRepository;
    }

    @Override
    public VoucherResponseDto createVoucher(VoucherRequestDto voucherDTO) {
        Product product = productRepository.findById(voucherDTO.getProductId())
            .orElseThrow(() -> new DataNotFoundException("category not found!"));
        Voucher voucher = new Voucher();
        voucher.setProduct(product);
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
        Product product = productRepository.findById(voucherDTO.getProductId())
            .orElseThrow(() -> new DataNotFoundException("product not found!"));
        existingVoucher.setProduct(product);
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
        existingVoucher.softDelete();
        voucherRepository.save(existingVoucher);
    }

}
