package com.grocery.quickbasket.carts.controller;

import java.util.List;

import com.grocery.quickbasket.carts.dto.*;
import com.grocery.quickbasket.carts.repository.CartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping
    public ResponseEntity<?> deleteCart(@RequestBody CartDeleteRequestDto request) {
        cartService.deleteCartByUserIdAndInventoryIds(request.getUserId(), request.getInventoryIds());
        return Response.successResponse("delete success", null);
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

    @DeleteMapping("/carts/{storeId}")
    public ResponseEntity<?> deleteAllCart (@PathVariable Long storeId) {
        cartService.deleteAllCartByUserIdAndStoreId(storeId);
        return ResponseEntity.ok().build();
    }
}
