package com.grocery.quickbasket.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderListDetailDto {
    private List<OrderDetailDto> orders;
    private Pagination pagination;

    @Data
    @Builder
    public static class OrderDetailDto {
        private Long id;
        private String orderCode;
        private String storeName;
        private BigDecimal totalAmount;
        private String orderStatus;
        private Instant createdAt;
        private String shippingMethod;
        private PaymentDto payment;
        private List<OrderItemDto> items;
    }

    @Data
    @Builder
    public static class OrderItemDto {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @Builder
    public static class PaymentDto {
        private String paymentMethod;
        private String paymentStatus;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int itemsPerPage;
    }
}
