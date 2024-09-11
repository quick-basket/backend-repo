package com.grocery.quickbasket.carts.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.CartListResponseDto;
import com.grocery.quickbasket.carts.dto.CartListSummaryResponseDto;
import com.grocery.quickbasket.carts.dto.CartRequestDto;
import com.grocery.quickbasket.carts.dto.CartResponseDto;
import com.grocery.quickbasket.carts.dto.CartSummaryResponseDto;
import com.grocery.quickbasket.carts.entity.Cart;
import com.grocery.quickbasket.carts.repository.CartRepository;
import com.grocery.quickbasket.carts.service.CartService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.productImages.entity.ProductImage;
import com.grocery.quickbasket.productImages.repository.ProductImageRepository;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;

    public CartServiceImpl (CartRepository cartRepository, UserRepository userRepository, InventoryRepository inventoryRepository, ProductImageRepository productImageRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public List<CartListResponseDto> getAllCartByUserId() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
            .map(cart -> {
                Product product = cart.getInventory().getProduct();
                
                List<String> imageUrls = productImageRepository.findAllByProduct(product)
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());

                return CartListResponseDto.mapToDto(cart, imageUrls);
            })
            .collect(Collectors.toList());
    }



    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public CartResponseDto createCart(CartRequestDto requestDto) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");
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

    @Override
    public CartSummaryResponseDto getCartSummary() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<Cart> carts = cartRepository.findAllByUserId(userId);

        List<CartListSummaryResponseDto> cartList = carts.stream()
            .map(CartListSummaryResponseDto::mapToDto)
            .collect(Collectors.toList());

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscountPrice = BigDecimal.ZERO;

        for (CartListSummaryResponseDto cart : cartList) {
            BigDecimal itemTotalPrice = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalPrice = totalPrice.add(itemTotalPrice);

            BigDecimal itemTotalDiscountPrice = cart.getDiscountPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalDiscountPrice = totalDiscountPrice.add(itemTotalDiscountPrice);
        }
        BigDecimal totalDiscount = totalPrice.subtract(totalDiscountPrice);

        return new CartSummaryResponseDto(cartList, totalPrice, totalDiscount, totalDiscountPrice);
    }

}
