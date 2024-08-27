package com.grocery.quickbasket.discounts.service;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.discounts.dto.DiscountListResponseDto;
import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
import com.grocery.quickbasket.discounts.dto.DiscountResponseDto;
import com.grocery.quickbasket.discounts.entity.Discount;

public interface DiscountService {

    List<DiscountListResponseDto> getAllDiscounts();
    Optional<Discount> getDiscountById(Long id);
    DiscountResponseDto createDiscount(DiscountRequestDto requestDto);
    DiscountResponseDto updateDiscount(Long id, DiscountRequestDto requestDto);
    void deleteDiscount(Long id);
}
