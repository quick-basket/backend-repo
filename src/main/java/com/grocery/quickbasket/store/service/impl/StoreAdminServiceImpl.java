package com.grocery.quickbasket.store.service.impl;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.store.entity.StoreAdmin;
import com.grocery.quickbasket.store.repository.StoreAdminRepository;
import com.grocery.quickbasket.store.service.StoreAdminService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreAdminServiceImpl implements StoreAdminService {
    private final StoreAdminRepository repository;

    public StoreAdminServiceImpl(StoreAdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long getStoreIdForUser(Long userId) {
        return repository.findByUserId(userId)
                .map(storeAdmin1 -> storeAdmin1.getStore().getId())
                .orElseThrow(() -> new DataNotFoundException("Store admin not found"));
    }
}
