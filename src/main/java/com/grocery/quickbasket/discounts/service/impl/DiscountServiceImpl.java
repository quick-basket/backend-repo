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
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository, StoreRepository storeRepository, ProductRepository productRepository) { 
        this.discountRepository = discountRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<DiscountListResponseDto> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountListResponseDto> discountDtos = discounts.stream()
            .map(DiscountListResponseDto::fromEntity)
            .collect(Collectors.toList());

        return discountDtos; 
    }

    @Override
    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    @Override
    public DiscountResponseDto createDiscount(DiscountRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + requestDto.getStoreId()));
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new DataNotFoundException("product not found with id " + requestDto));
        
        Discount discount = new Discount();
        discount.setStore(store);
        discount.setProduct(product);
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
        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + requestDto.getStoreId()));
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + requestDto.getProductId()));
        
        existingDiscount.setStore(store);
        existingDiscount.setProduct(product);
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
