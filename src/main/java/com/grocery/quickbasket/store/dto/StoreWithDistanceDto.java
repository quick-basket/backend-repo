package com.grocery.quickbasket.store.dto;

import lombok.Data;

@Data
public class StoreWithDistanceDto {
    private StoreDto store;
    private double distance;
}
