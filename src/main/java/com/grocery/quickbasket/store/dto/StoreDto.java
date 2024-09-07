package com.grocery.quickbasket.store.dto;

import com.grocery.quickbasket.store.entity.Store;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.Optional;

@Data
public class StoreDto {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private double latitude;
    private double longitude;
    private Instant createdAt;
    private Instant updatedAt;

    public static StoreDto fromEntity(Store store) {
        StoreDto dto = new StoreDto();
        BeanUtils.copyProperties(store, dto);
        // Using Optional to handle potential null values for location
        Optional.ofNullable(store.getLocation()).ifPresent(location -> {
            dto.setLongitude(location.getX());
            dto.setLatitude(location.getY());
        });
        return dto;
    }

    public static Store toEntity(StoreDto dto) {
        Store store = new Store();
        BeanUtils.copyProperties(dto, store);
        return store;
    }
}



