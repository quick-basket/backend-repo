package com.grocery.quickbasket.discounts.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.discounts.dto.DiscountListResponseDto;
import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
import com.grocery.quickbasket.discounts.dto.DiscountResponseDto;
import com.grocery.quickbasket.discounts.entity.Discount;
import com.grocery.quickbasket.discounts.repository.DiscountRepository;
import com.grocery.quickbasket.discounts.service.DiscountService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final InventoryRepository inventoryRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository, InventoryRepository inventoryRepository) { 
        this.discountRepository = discountRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<DiscountListResponseDto> getAllDiscountsByStoreId(Long storeId) {
        List<Discount> discounts = discountRepository.findAllByStoreId(storeId);
        return discounts.stream()
            .map(DiscountListResponseDto::fromEntity)
            .collect(Collectors.toList());
        
    }

    @Override
    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    @Override
    public DiscountResponseDto createDiscount(DiscountRequestDto requestDto) {
        Inventory inventory = inventoryRepository.findById(requestDto.getInventoryId())
            .orElseThrow(() -> new DataNotFoundException("Inventory not found for this id :: " + requestDto.getInventoryId()));        
        Discount discount = new Discount();
        discount.setInventory(inventory);
        discount.setType(requestDto.getTypeAsEnum());
        discount.setValue(requestDto.getValue());
        discount.setMinPurchase(requestDto.getMinPurchase());
        discount.setMaxDiscount(requestDto.getMaxDiscount());
        discount.setStartDate(requestDto.getStartDate());
        discount.setEndDate(requestDto.getEndDate());
        Discount savedDiscount = discountRepository.save(discount);

        return DiscountResponseDto.formDiscount(savedDiscount);
    }

    @Override
    public DiscountResponseDto updateDiscount(Long id, DiscountRequestDto requestDto) {
        Discount existingDiscount = discountRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + id));
        Inventory inventory = inventoryRepository.findById(requestDto.getInventoryId())
            .orElseThrow(() -> new DataNotFoundException("Inventory not found for this id :: " + requestDto.getInventoryId()));        
        
        existingDiscount.setInventory(inventory);
        existingDiscount.setType(requestDto.getTypeAsEnum());
        existingDiscount.setValue(requestDto.getValue());
        existingDiscount.setMinPurchase(requestDto.getMinPurchase());
        existingDiscount.setMaxDiscount(requestDto.getMaxDiscount());
        existingDiscount.setStartDate(requestDto.getStartDate());
        existingDiscount.setEndDate(requestDto.getEndDate());
        Discount updatedDiscount = discountRepository.save(existingDiscount);

        return DiscountResponseDto.formDiscount(updatedDiscount);
    }

    @Override
    public void deleteDiscount(Long id) {
        Discount existingDiscountt = discountRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Discount not found for this id :: " + id));
        discountRepository.delete(existingDiscountt);
    }

}
