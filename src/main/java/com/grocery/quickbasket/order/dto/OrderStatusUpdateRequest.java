package com.grocery.quickbasket.order.dto;

import com.grocery.quickbasket.order.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    private OrderStatus newStatus;
}
