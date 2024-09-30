package com.grocery.quickbasket.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class OrderWithMidtransResponseDto {
    private OrderResponseDto order;
    private Map<String, Object> midtransResponse;
}
