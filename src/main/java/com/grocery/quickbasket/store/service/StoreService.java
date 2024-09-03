package com.grocery.quickbasket.store.service;

import java.util.List;

import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.entity.Store;

public interface StoreService {
    List<Store> getAllStores();
    Store getStoreById(Long id);
    Store addStore(StoreDto dto);
    Store updateStore(StoreDto dto);
    String deleteStore(Long id);
}
