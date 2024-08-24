package com.grocery.quickbasket.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.inventory.dto.InventoryRequestDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestUpdateDto;
import com.grocery.quickbasket.inventory.dto.InventoryResponseDto;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.inventory.service.InventoryService;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    
    public InventoryServiceImpl (InventoryRepository inventoryRepository, ProductRepository productRepository, StoreRepository storeRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository= productRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public InventoryResponseDto craeteInventory(InventoryRequestDto inventoryRequestDto) {
        Product product = productRepository.findById(inventoryRequestDto.getProductId())
            .orElseThrow(() -> new RuntimeException("category not found!"));
        Store store = storeRepository.findById(inventoryRequestDto.getStoreId())
            .orElseThrow(() -> new RuntimeException("category not found!"));
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setStore(store);
        inventory.setQuantity(inventoryRequestDto.getQuantity());
        inventoryRepository.save(inventory);
        return InventoryResponseDto.mapToDto(inventory);
    }

    @Override
    public List<Inventory> getAllProductCategory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory updateInventory(Long id, InventoryRequestUpdateDto updateDto) {
        Optional<Inventory> existingInventory = inventoryRepository.findById(id);
        if (existingInventory.isPresent()) {
            Inventory inventoryUpdate = existingInventory.get();
            Product product = productRepository.findById(updateDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id " + updateDto.getProductId()));
            Store store = storeRepository.findById(updateDto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found with id " + updateDto.getStoreId()));
            inventoryUpdate.setProduct(product);
            inventoryUpdate.setStore(store);
            inventoryUpdate.setQuantity(updateDto.getQuantity());
            return inventoryRepository.save(inventoryUpdate);
        } else {
            throw new RuntimeException("product category not found with id " + id);
        }
    }

    @Override
    public void deleteInventory(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("product category not found with id " + id);
        }
    }

}
