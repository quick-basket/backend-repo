package com.grocery.quickbasket.inventory.service;

import java.util.List;

import com.grocery.quickbasket.inventory.dto.InventoryDetailResponseDto;
import com.grocery.quickbasket.inventory.dto.InventoryListResponseDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestUpdateDto;
import com.grocery.quickbasket.inventory.dto.InventoryResponseDto;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.order.entity.Order;

public interface InventoryService {

    InventoryResponseDto craeteInventory (InventoryRequestDto inventoryRequestDto);
    InventoryResponseDto updateInventory (Long id, InventoryRequestUpdateDto updateDto);
    List<Inventory> getAllProductCategory();
    InventoryDetailResponseDto getProductById (Long inventoryId);
    List<InventoryListResponseDto> getInventoryByStoreId(Long storeId);
    List<InventoryListResponseDto> getInventoryWithoutDiscountsByStoreId(Long storeId);
    void deleteInventory(Long id);
    void deleteStock(Order order);
}
