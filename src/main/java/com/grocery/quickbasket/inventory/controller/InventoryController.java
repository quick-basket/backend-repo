package com.grocery.quickbasket.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.dto.InventoryListResponseDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestDto;
import com.grocery.quickbasket.inventory.dto.InventoryRequestUpdateDto;
import com.grocery.quickbasket.inventory.dto.InventoryResponseDto;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.service.InventoryService;
import com.grocery.quickbasket.response.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequestDto inventoryRequestDto) {
        InventoryResponseDto responseDto = inventoryService.craeteInventory(inventoryRequestDto);
        return Response.successResponse("invetory created", responseDto);
    }
    @GetMapping()
    public ResponseEntity<?> getAllInventory() {
        List<Inventory> inventories = inventoryService.getAllProductCategory();
        return Response.successResponse("fetch all inventories", inventories);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getInventoryByStoreId(@PathVariable Long storeId) {
        List<InventoryListResponseDto> inventoryList = inventoryService.getInventoryByStoreId(storeId);
        return Response.successResponse("fetch inventory by store id", inventoryList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory (@PathVariable Long id, @Valid @RequestBody InventoryRequestUpdateDto updateDto) {
        InventoryResponseDto updatedInventory = inventoryService.updateInventory(id, updateDto);
        return Response.successResponse("inventory updated", updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataNotFoundException e ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
