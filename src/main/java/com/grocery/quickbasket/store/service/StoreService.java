package com.grocery.quickbasket.store.service;

import java.util.List;

import com.grocery.quickbasket.store.dto.StoreAdminDto;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreResponseDto;
import com.grocery.quickbasket.store.entity.Store;

import org.locationtech.jts.geom.Point;

public interface StoreService {
    List<StoreDto> getAllStores();
    StoreDto getStoreById(Long id);
    StoreDto addStore(StoreDto dto);
    StoreDto updateStore(StoreDto dto);
    String deleteStore(Long id);
    Store findNearestStore(double longitude, double latitude);
    double calculateDistance(Long storeId, Point userLocation);
    List<StoreResponseDto> getAllStoreNotInStoreAdmins();
    List<StoreAdminDto> getAllStoreAdmins();
    void deleteStoreAdmin(Long storeAdminId);
}
