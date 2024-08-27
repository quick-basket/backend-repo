package com.grocery.quickbasket.discounts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.discounts.dto.DiscountListResponseDto;
import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
import com.grocery.quickbasket.discounts.dto.DiscountResponseDto;
import com.grocery.quickbasket.discounts.entity.Discount;
import com.grocery.quickbasket.discounts.service.DiscountService;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController (DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDiscount(@RequestBody DiscountRequestDto requestDto) {
        DiscountResponseDto createdDiscount = discountService.createDiscount(requestDto);
        return new ResponseEntity<>(createdDiscount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable Long id, @RequestBody DiscountRequestDto requestDto) {
        DiscountResponseDto updatedDiscount = discountService.updateDiscount(id, requestDto);
        return ResponseEntity.ok(updatedDiscount);
    }

    @GetMapping
    public ResponseEntity<?> getAllDiscount() {
        List<DiscountListResponseDto> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }
}
