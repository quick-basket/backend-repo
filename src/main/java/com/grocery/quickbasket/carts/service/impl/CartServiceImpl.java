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
import com.grocery.quickbasket.discounts.entity.Discount;
import com.grocery.quickbasket.discounts.repository.DiscountRepository;
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

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final DiscountRepository discountRepository;

    public CartServiceImpl (CartRepository cartRepository, UserRepository userRepository, InventoryRepository inventoryRepository, ProductImageRepository productImageRepository, UserVoucherRepository userVoucherRepository, DiscountRepository discountRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.productImageRepository = productImageRepository;
        this.userVoucherRepository = userVoucherRepository;
        this.discountRepository = discountRepository;
    }

    @Override
    public List<CartListResponseDto> getAllCartByUserId() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
            .map(cart -> {
                List<Discount> discounts = cart.getInventory().getDiscount();
            List<String> imageUrls = productImageRepository.findAllByProduct(cart.getInventory().getProduct())
                .stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
            return CartListResponseDto.mapToDto(cart, discounts, imageUrls);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<CartListResponseDto> getAllCartByUserIdWithStoreId(Long storeId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");
        List<Cart> carts = cartRepository.findAllByUserIdAndInventoryStoreId(userId, storeId);
        return carts.stream()
            .map(cart -> {
                List<Discount> discounts = cart.getInventory().getDiscount();
                List<String> imageUrls = productImageRepository.findAllByProduct(cart.getInventory().getProduct())
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
                return CartListResponseDto.mapToDto(cart, discounts, imageUrls);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllCartByUserIdAndStoreId(Long storeId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        cartRepository.deleteAllByUserIdAndInventoryStoreId(userId, storeId);
    }



    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    @Transactional
    public CartResponseDto createCart(CartRequestDto requestDto) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found for this id :: " + userId));

        Inventory inventory = inventoryRepository.findById(requestDto.getInventoryId())
            .orElseThrow(() -> new DataNotFoundException("Store not found for this id :: " + requestDto.getInventoryId()));

        Product product = inventory.getProduct();
        if (product == null) {
            throw new DataNotFoundException("Product not found in inventory :: " + requestDto.getInventoryId());
        }

        Optional<Cart> existingCart = cartRepository.findByUserIdAndInventoryId(userId, requestDto.getInventoryId());

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + requestDto.getQuantity());
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setInventory(inventory);
            cart.setPrice(product.getPrice());
            cart.setQuantity(requestDto.getQuantity());
        }

        List<Discount> discounts = discountRepository.findByInventoryId(requestDto.getInventoryId());
        boolean isBOGO = false;
        BigDecimal discountPrice = product.getPrice();
        if (!discounts.isEmpty()) {
            Discount discount = discounts.get(0);
            switch (discount.getType()) {
                case PERCENTAGE:
                    BigDecimal discountValue = discount.getValue();
                    discountPrice = product.getPrice().subtract(product.getPrice().multiply(discountValue.divide(new BigDecimal(100))));
                    cart.setDiscountPrice(discountPrice.setScale(2, RoundingMode.HALF_DOWN));

                    break;
                case FIXED:
                    discountPrice = product.getPrice().subtract(discount.getValue());
                    cart.setDiscountPrice(discountPrice);
                    
                case BUY_ONE_GET_ONE:
                    discountPrice = product.getPrice();
                    cart.setDiscountPrice(discountPrice);
                    isBOGO = true;
                    break;
            }
        }

        cart.setDiscountPrice(discountPrice.setScale(2, RoundingMode.HALF_DOWN));

        Cart savedCart = cartRepository.save(cart);
        if (isBOGO) {
            Integer currentBonusItem = inventory.getBonusItem() != null ? inventory.getBonusItem() : 0;
            inventory.setBonusItem(currentBonusItem + 1);
            inventoryRepository.save(inventory);
        }
        CartResponseDto responseDto = CartResponseDto.mapToDto(savedCart);
        if (isBOGO) {
            responseDto.setQuantityBonus(savedCart.getQuantity() + 1);
        } else {
            responseDto.setQuantityBonus(savedCart.getQuantity());
        }
        return responseDto;
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
    public void deleteCartByUserIdAndInventoryIds(Long userId, List<Long> inventoryIds) {
        cartRepository.deleteByUserIdAndInventoryIdIn(userId, inventoryIds);
    }

    @Override
    public CartSummaryResponseDto getCartSummary(Long storeId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<Cart> carts = cartRepository.findAllByUserIdAndInventoryStoreId(userId, storeId);

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
