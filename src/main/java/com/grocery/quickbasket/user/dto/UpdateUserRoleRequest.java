package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.user.entity.Role;

import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    private Long userId;
    private Role newRole;
    private Long storeId;
}
