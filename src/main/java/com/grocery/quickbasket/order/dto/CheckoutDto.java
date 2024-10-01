package com.grocery.quickbasket.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutDto {
    private Long userId;
    private Long storeId;
    private String storeName;
    private Recipient recipient;
    private List<Item> items;
    private Summary summary;
    private String shippingMethod;
    private String orderCode;

    @Data
    public static class Recipient {
        private Long addressId;
        private String name;
        private String phone;
        private String fullAddress;
        private String city;
        private String postalCode;
    }

    @Data
    public static class Item {
        private Long productId;
        private String name;
        private String image;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private int quantity;
        private BigDecimal subtotal;
    }

    @Data
    public static class Summary {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal voucher;
        private BigDecimal shippingCost;
        private BigDecimal total;
    }
}
