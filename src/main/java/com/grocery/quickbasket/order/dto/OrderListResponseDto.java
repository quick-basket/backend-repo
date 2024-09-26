package com.grocery.quickbasket.order.dto;


import java.math.BigDecimal;
import java.time.Instant;

import com.grocery.quickbasket.order.entity.Order;

import lombok.Data;

@Data
public class OrderListResponseDto {

    private Long id;
    private Long storeId;
    private Long userId;
    private String storeName;
    private BigDecimal totalAmount;
    private String OrderStatus;
    private String orderCode;
    private Instant createdAt;

    public static OrderListResponseDto mapToDto (Order order) {
        OrderListResponseDto dto = new OrderListResponseDto();
        dto.setId(order.getId());
        dto.setStoreId(order.getStore().getId());
        dto.setUserId(order.getUserId());
        dto.setStoreName(order.getStore().getName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getStatus().name());
        dto.setOrderCode(order.getOrderCode());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

}
