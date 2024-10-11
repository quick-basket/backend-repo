package com.grocery.quickbasket.store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreWithDistanceDto {
    private StoreDto store;
    private double distance;
    private BigDecimal deliveryCost;
}
