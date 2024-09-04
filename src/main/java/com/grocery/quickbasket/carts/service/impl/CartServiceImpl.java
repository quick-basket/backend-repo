package com.grocery.quickbasket.carts.service.impl;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.CartRequestDto;
import com.grocery.quickbasket.carts.dto.CartResponseDto;
import com.grocery.quickbasket.carts.entity.Cart;
import com.grocery.quickbasket.carts.repository.CartRepository;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    public CartServiceImpl (CartRepository cartRepository, UserRepository userRepository, InventoryRepository inventoryRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<CartResponseDto> getAllCartByUserId(Long userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
            .map(CartResponseDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public CartResponseDto createCart(CartRequestDto requestDto) {
        Map<String, Object> claims = Claims.getClaimsFromJwt();
        Long userId = Long.parseLong(claims.get("userId").toString());
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found for this id :: " + userId));

        Inventory inventory = inventoryRepository.findById(requestDto.getInventoryId())
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + requestDto.getInventoryId()));
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setInventory(inventory);
        cart.setPrice(requestDto.getPrice());
        cart.setDiscountPrice(requestDto.getDiscountPrice());
        cart.setQuantity(requestDto.getQuantity());
        Cart savedCart = cartRepository.save(cart);
        return CartResponseDto.mapToDto(savedCart);
    }

    @Override
    public CartResponseDto updateCart(Long id, CartRequestDto requestDto) {
        Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + id));
        cart.setQuantity(requestDto.getQuantity());
        Cart updatedCart = cartRepository.save(cart);
        return CartResponseDto.mapToDto(updatedCart);
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

}
