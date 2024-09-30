package com.grocery.quickbasket.store.dto;

import com.grocery.quickbasket.store.entity.StoreAdmin;

import lombok.Data;

@Data
public class StoreAdminDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long storeId;
    private String storeName;

    public static StoreAdminDto mapToDto (StoreAdmin storeAdmin) {
        StoreAdminDto dto = new StoreAdminDto();
        dto.setId(storeAdmin.getId());
        dto.setUserId(storeAdmin.getUser().getId());
        dto.setUserName(storeAdmin.getUser().getName());
        dto.setStoreId(storeAdmin.getStore().getId());
        dto.setStoreName(storeAdmin.getStore().getName());
        return dto;
    }
}
