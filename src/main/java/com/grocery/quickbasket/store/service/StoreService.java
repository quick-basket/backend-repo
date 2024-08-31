package com.grocery.quickbasket.store.service;

import java.util.List;

import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.entity.Store;

public interface StoreService {
    List<StoreDto> getAllStores();
    StoreDto getStoreById(Long id);
    StoreDto addStore(StoreDto dto);
    StoreDto updateStore(StoreDto dto);
    String deleteStore(Long id);
}
