package com.grocery.quickbasket.carts.service;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.carts.dto.CartRequestDto;
import com.grocery.quickbasket.carts.dto.CartResponseDto;
import com.grocery.quickbasket.carts.entity.Cart;

public interface CartService {
    List<CartResponseDto> getAllCartByUserId(Long userId);
    Optional<Cart> getCartById(Long id);
    CartResponseDto createCart(CartRequestDto requestDto);
    CartResponseDto updateCart(Long id, CartRequestDto requestDto);
    void deleteCart(Long id);
}
