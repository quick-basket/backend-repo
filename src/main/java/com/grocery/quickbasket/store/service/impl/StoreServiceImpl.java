package com.grocery.quickbasket.store.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.store.dto.StoreDto;
import org.springframework.stereotype.Service;

import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;

    public StoreServiceImpl (StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<StoreDto> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreDto::fromEntity)
                .toList();
    }

    @Override
    public StoreDto getStoreById(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        if (store.isEmpty()) {
            throw new StoreNotFoundException("Store not found");
        }
        return StoreDto.fromEntity(store.get());
    }

    @Override
    public StoreDto addStore(StoreDto dto) {
        Store store = StoreDto.toEntity(dto);
        Store savedStore = storeRepository.save(store);

        return StoreDto.fromEntity(savedStore);
    }

    @Override
    public StoreDto updateStore(StoreDto dto) {
        Optional<Store> storeOptional = storeRepository.findById(dto.getId());
        if (storeOptional.isEmpty()) {
            throw new StoreNotFoundException("Store not found");
        }
        Store currentStore = storeOptional.get();

        currentStore.updateFromDto(dto);

        Store savedStore = storeRepository.save(currentStore);

        return StoreDto.fromEntity(savedStore);

    }

    @Override
    public String deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException("Store not found");
        }
        storeRepository.deleteById(id);
        return "Delete successfully";
    }


}
