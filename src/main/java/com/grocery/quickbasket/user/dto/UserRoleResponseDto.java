package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.store.entity.StoreAdmin;
import com.grocery.quickbasket.user.entity.Role;
import com.grocery.quickbasket.user.entity.User;

import lombok.Data;

@Data
public class UserRoleResponseDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private Long storeId;

    public static UserRoleResponseDto convertToDto(User user, StoreAdmin storeAdmin) {
        UserRoleResponseDto dto = new UserRoleResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        if (storeAdmin != null && storeAdmin.getStore() != null) {
            dto.setStoreId(storeAdmin.getStore().getId());
        }
        return dto;
    }
}
