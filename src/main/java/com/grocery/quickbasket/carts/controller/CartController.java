package com.grocery.quickbasket.carts.controller;

import java.util.List;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.entity.Cart;
import com.grocery.quickbasket.carts.repository.CartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartRequestDto;
import com.grocery.quickbasket.carts.dto.CartResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.response.Response;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;

    public CartController (CartService cartService, CartRepository cartRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    @PostMapping()
    public ResponseEntity<?> createCart(@RequestBody CartRequestDto requestDto) {
        CartResponseDto createdCart = cartService.createCart(requestDto);
        return Response.successResponse("cart created", createdCart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart (@PathVariable Long id, @RequestBody CartRequestDto requestDto) {
        CartResponseDto updatedCart = cartService.updateCart(id, requestDto);
        return Response.successResponse("cart updated", updatedCart);
    }

    @GetMapping()
    public ResponseEntity<?> getAllCartByUserId() {
        List<CartListResponseDto> cartResponseDtos = cartService.getAllCartByUserId();
        return Response.successResponse("fetched all carts", cartResponseDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{storeId}")
    public ResponseEntity<?> getCartSummary(@PathVariable Long storeId) {
        CartSummaryResponseDto responseDto = cartService.getCartSummary(storeId);
        return Response.successResponse("fetched all carts", responseDto);
    }

    @GetMapping("/cart-store/{storeId}")
    public ResponseEntity<?> getCartWithStoreId(@PathVariable Long storeId) {
        return Response.successResponse("fetched all carts", cartService.getAllCartByUserIdWithStoreId(storeId));
    }
}
