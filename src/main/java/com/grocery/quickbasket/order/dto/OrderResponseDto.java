package com.grocery.quickbasket.order.dto;

import java.math.BigDecimal;

import com.grocery.quickbasket.order.entity.Order;

import lombok.Data;

@Data
public class OrderResponseDto {
private Long id;
    private Long storeId;
    private Long userId;
    private String storeName;
    private BigDecimal totalAmount;
    private String OrderStatus;
    private String orderCode;

    public OrderResponseDto mapToDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setStoreId(order.getStore().getId());
        dto.setStoreName(order.getStore().getName());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getStatus().name());
        dto.setOrderCode(order.getOrderCode());
        return dto;
    }
}
