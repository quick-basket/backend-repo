package com.grocery.quickbasket.inventory.service;

import java.util.List;
import com.grocery.quickbasket.inventory.dto.InventoryRequestDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestUpdateDto;
import com.grocery.quickbasket.inventory.dto.InventoryResponseDto;
import com.grocery.quickbasket.inventory.entity.Inventory;

public interface InventoryService {

    InventoryResponseDto craeteInventory (InventoryRequestDto inventoryRequestDto);
    Inventory updateInventory (Long id, InventoryRequestUpdateDto updateDto);
    List<Inventory> getAllProductCategory();
    void deleteInventory(Long id);
}
