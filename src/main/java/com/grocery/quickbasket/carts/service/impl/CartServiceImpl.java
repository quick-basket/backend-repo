package com.grocery.quickbasket.carts.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.carts.dto.AvailableUserVoucherDto;
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
import com.grocery.quickbasket.vouchers.entity.DiscountTypes;
import com.grocery.quickbasket.vouchers.entity.UserVoucher;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.repository.UserVoucherRepository;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    private final UserVoucherRepository userVoucherRepository;

    public CartServiceImpl (CartRepository cartRepository, UserRepository userRepository, InventoryRepository inventoryRepository, ProductImageRepository productImageRepository, UserVoucherRepository userVoucherRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.productImageRepository = productImageRepository;
        this.userVoucherRepository = userVoucherRepository;
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
public CartSummaryResponseDto getCartSummary(Long selectedUserVoucherId) {
    var claims = Claims.getClaimsFromJwt();
    Long userId = (Long) claims.get("userId");

    List<Cart> carts = cartRepository.findAllByUserId(userId);

    List<CartListSummaryResponseDto> cartList = carts.stream()
        .map(CartListSummaryResponseDto::mapToDto)
        .collect(Collectors.toList());

    BigDecimal totalPrice = BigDecimal.ZERO; 
    BigDecimal totalDiscountPrice = BigDecimal.ZERO; 
    BigDecimal totalDiscount = BigDecimal.ZERO; 

    for (CartListSummaryResponseDto cart : cartList) {
        BigDecimal itemTotalPrice = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
        totalPrice = totalPrice.add(itemTotalPrice);

        BigDecimal itemTotalDiscountPrice = cart.getDiscountPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
        totalDiscountPrice = totalDiscountPrice.add(itemTotalDiscountPrice);
    }

    totalDiscount = totalPrice.subtract(totalDiscountPrice);

    List<AvailableUserVoucherDto> availableVouchers = getAvailableVouchers(userId, totalPrice);

    AvailableUserVoucherDto selectedVoucher = null;
    if (selectedUserVoucherId != null) {
        selectedVoucher = availableVouchers.stream()
            .filter(v -> v.getUserVoucherId().equals(selectedUserVoucherId))
            .findFirst()
            .orElse(null);

        if (selectedVoucher != null) {
            BigDecimal voucherDiscount = applyVoucher(userId, totalDiscountPrice, cartList, selectedUserVoucherId);

            totalDiscount = totalDiscount.add(voucherDiscount);
            totalDiscountPrice = totalDiscountPrice.subtract(voucherDiscount);
        }
    }

    return new CartSummaryResponseDto(cartList, totalPrice, totalDiscount, totalDiscountPrice, availableVouchers, selectedVoucher);
}

    
public BigDecimal applyVoucher(Long userId, BigDecimal totalPrice, List<CartListSummaryResponseDto> cartList, Long userVoucherId) {
    try {
        UserVoucher userVoucher = userVoucherRepository.findByIdAndUserIdAndIsUsedFalse(userVoucherId, userId)
            .orElseThrow(() -> new DataNotFoundException("Voucher tidak ditemukan atau sudah digunakan"));

        Voucher voucher = userVoucher.getVoucher();

        Instant now = Instant.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate()) 
            || (voucher.getMinPurchase() != null && totalPrice.compareTo(voucher.getMinPurchase()) < 0)) {
            throw new IllegalArgumentException("Voucher tidak memenuhi syarat");
        }

        BigDecimal voucherDiscount = calculateVoucherDiscount(voucher, totalPrice, cartList);
        userVoucher.setIsUsed(true);
        userVoucher.setUsedAt(now);
        userVoucherRepository.save(userVoucher);

        return voucherDiscount;
    } catch (Exception e) {
        return BigDecimal.ZERO;
    }
}
    

    private BigDecimal calculateVoucherDiscount(Voucher voucher, BigDecimal totalDiscountPrice, List<CartListSummaryResponseDto> cartList) {
        switch (voucher.getVoucherType()) {
            case CART_TOTAL:
            case REFERRAL:
            if (voucher.getDiscountType() == DiscountTypes.PERCENTAGE) {
                BigDecimal discountPercentage = voucher.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal discountAmount = totalDiscountPrice.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
                return discountAmount;
            } else if (voucher.getDiscountType() == DiscountTypes.FIXED) {
                totalDiscountPrice = totalDiscountPrice.subtract(voucher.getDiscountValue());
                return voucher.getDiscountValue();
            }
                break;
    
            case PRODUCT_SPECIFIC:
                if (voucher.getProduct() != null && voucher.getProduct().getId() != null) {
                    return cartList.stream()
                        .filter(cart -> cart.getProductId().equals(voucher.getProduct().getId()))
                        .map(cart -> {
                            BigDecimal itemPrice = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                            if (voucher.getDiscountType() == DiscountTypes.PERCENTAGE) {
                                return itemPrice.multiply(voucher.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                            } else if (voucher.getDiscountType() == DiscountTypes.FIXED) {
                                return voucher.getDiscountValue().multiply(BigDecimal.valueOf(cart.getQuantity()));
                            }
                            return BigDecimal.ZERO;
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
                break;
    
            case SHIPPING:
                return BigDecimal.ZERO; 
    
            default:
                throw new IllegalArgumentException("Jenis voucher tidak valid");
        }
        return BigDecimal.ZERO;
    }
    private List<AvailableUserVoucherDto> getAvailableVouchers(Long userId, BigDecimal totalPrice) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUserIdAndIsUsedFalse(userId);
        Instant now = Instant.now();
    
        return userVouchers.stream()
            .filter(userVoucher -> {
                Voucher voucher = userVoucher.getVoucher();
                return now.isAfter(voucher.getStartDate()) && now.isBefore(voucher.getEndDate())
                && (voucher.getMinPurchase() == null || totalPrice.compareTo(voucher.getMinPurchase()) >= 0);
            })
            .map(this::mapToAvailableVoucherDto)
            .collect(Collectors.toList());
    }
    
    private AvailableUserVoucherDto mapToAvailableVoucherDto(UserVoucher userVoucher) {
        Voucher voucher = userVoucher.getVoucher();
        AvailableUserVoucherDto dto = new AvailableUserVoucherDto();
        dto.setUserVoucherId(userVoucher.getId());
        dto.setVoucherId(voucher.getId());
        dto.setVoucherCode(voucher.getCode());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setDiscountType(voucher.getDiscountType());
        dto.setVoucherType(voucher.getVoucherType());
        dto.setMinPurchase(voucher.getMinPurchase());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        return dto;
    }
}
