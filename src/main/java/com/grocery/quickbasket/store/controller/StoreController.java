package com.grocery.quickbasket.store.controller;

import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.store.dto.StoreAdminDto;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreResponseDto;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grocery.quickbasket.store.service.StoreService;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController (StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllStores(Pageable pageable) {
        return Response.successResponse("get all stores", storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable Long id) {
        return Response.successResponse("get store by id", storeService.getStoreById(id));
    }

    @PostMapping
    public ResponseEntity<?> addStore(@RequestBody StoreDto storeDto) {
        return Response.successResponse("add store", storeService.addStore(storeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(@PathVariable Long id, @RequestBody StoreDto storeDto) {
        storeDto.setId(id); // Ensure the ID in the DTO matches the path variable
        return Response.successResponse("update store", storeService.updateStore(storeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStore(@PathVariable Long id) {
        return Response.successResponse("delete store", storeService.deleteStore(id));
    }

    @GetMapping("/not-store-admins")
    public ResponseEntity<?> getAllUsersNotInStoreAdmins() {
        List<StoreResponseDto> stores = storeService.getAllStoreNotInStoreAdmins();
        return Response.successResponse("get all stores",stores);
    }
    @GetMapping("/store-admins")
    public ResponseEntity<?> getAllStoreAdmin() {
        List<StoreAdminDto> stores = storeService.getAllStoreAdmins();
        return Response.successResponse("get all stores",stores);
    }

    @DeleteMapping("/store-admin/{storeAdminId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long storeAdminId) {
        storeService.deleteStoreAdmin(storeAdminId);
        return ResponseEntity.noContent().build();
    }

}
