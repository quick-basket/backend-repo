package com.grocery.quickbasket.discounts.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.grocery.quickbasket.discounts.service.DiscountService;
import com.grocery.quickbasket.response.Response;

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
        return Response.successResponse("discount crated", createdDiscount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable Long id, @RequestBody DiscountRequestDto requestDto) {
        DiscountResponseDto updatedDiscount = discountService.updateDiscount(id, requestDto);
        return Response.successResponse("discount updated", updatedDiscount);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllDiscount(@PathVariable Long storeId) {
        List<DiscountListResponseDto> discounts = discountService.getAllDiscountsByStoreId(storeId);
        return Response.successResponse("fetched all discount", discounts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
