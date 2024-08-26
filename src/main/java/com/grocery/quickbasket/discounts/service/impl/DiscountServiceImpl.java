package com.grocery.quickbasket.discounts.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.discounts.dto.DiscountRequestDto;
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
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    @Override
    public Discount createDiscount(DiscountRequestDto requestDto) {
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
        discount.setMinPurchase(requestDto.getMaxDiscount());
        discount.setStartDate(requestDto.getStartDate());
        discount.setEndDate(requestDto.getEndDate());

        return discountRepository.save(discount);
    }

    @Override
    public Discount updateDiscount(Long id, Discount discount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDiscount'");
    }

    @Override
    public void deleteDiscount(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDiscount'");
    }

}
