package com.grocery.quickbasket.carts.service;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartRequestDto;
import com.grocery.quickbasket.carts.dto.CartResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.entity.Cart;

public interface CartService {
    List<CartListResponseDto> getAllCartByUserId();
    Optional<Cart> getCartById(Long id);
    void deleteCart(Long id);
    CartResponseDto createCart(CartRequestDto requestDto);
    CartResponseDto updateCart(Long id, CartRequestDto requestDto);
    void deleteCartByUserIdAndInventoryIds(Long userId, List<Long> inventoryIds);
    CartSummaryResponseDto getCartSummary(Long storeId);
    List<CartListResponseDto> getAllCartByUserIdWithStoreId(Long storeId);
    void deleteAllCartByUserIdAndStoreId(Long storeId);
}
