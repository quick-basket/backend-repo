package com.grocery.quickbasket.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SnapTokenResponse {
    private String token;
    private Long orderId;
    private String clientKey;
}
