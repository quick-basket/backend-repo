package com.grocery.quickbasket.store.service.impl;

import java.util.List;
import java.util.Optional;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.exceptions.StoreNameSameException;
import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.exceptions.UserIdNotFoundException;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.repository.StoreAdminRepository;
import org.springframework.stereotype.Service;

import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;
    private final StoreAdminRepository storeAdminRepository;

    public StoreServiceImpl (StoreRepository storeRepository, StoreAdminRepository storeAdminRepository) {
        this.storeRepository = storeRepository;
        this.storeAdminRepository = storeAdminRepository;
    }

    @Override
    public List<Store> getAllStores() {
        List<Store> stores = storeRepository.getStoreByDeletedAtIsNull();
        return stores.stream()
                .toList();
    }

    @Override
    public Store getStoreById(Long id) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        if (!storeAdminRepository.existsByUserId(userId)){
            throw new UserIdNotFoundException("User Id is not authorize");
        }

        return storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));
    }

    @Override
    public Store addStore(StoreDto dto) {
        boolean exist = storeRepository.existsByName(dto.getName());
        if (exist) {
            throw new StoreNameSameException("Store name already exist");
        }
        Store store = StoreDto.toEntity(dto);

        return storeRepository.save(store);
    }

    @Override
    public Store updateStore(StoreDto dto) {
        Optional<Store> storeOptional = storeRepository.findById(dto.getId());
        if (storeOptional.isEmpty()) {
            throw new StoreNotFoundException("Store not found");
        }
        Store currentStore = storeOptional.get();

        currentStore.updateFromDto(dto);

        return storeRepository.save(currentStore);

    }

    @Transactional
    @Override
    public String deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));

        store.softDelete();
        storeRepository.save(store);

        return "Delete successfully";

//        storeRepository.deleteById(id);
//        return "Delete successfully";
    }


}
