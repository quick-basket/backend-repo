package com.grocery.quickbasket.discounts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
import com.grocery.quickbasket.discounts.entity.Discount;
import com.grocery.quickbasket.discounts.service.DiscountService;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController (DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping("/crate")
    public ResponseEntity<?> createDiscount (@RequestBody DiscountRequestDto requestDto) {
        Discount discount = discountService.createDiscount(requestDto);
        return ResponseEntity.ok(discount);
    }
}
