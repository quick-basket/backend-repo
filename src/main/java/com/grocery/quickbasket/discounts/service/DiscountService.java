package com.grocery.quickbasket.discounts.service;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
import com.grocery.quickbasket.discounts.entity.Discount;

public interface DiscountService {

    List<Discount> getAllDiscounts();
    Optional<Discount> getDiscountById(Long id);
    Discount createDiscount(DiscountRequestDto requestDto);
    Discount updateDiscount(Long id, Discount discount);
    void deleteDiscount(Long id);
}
