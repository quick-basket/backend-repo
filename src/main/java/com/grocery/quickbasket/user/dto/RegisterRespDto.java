package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.user.entity.User;
import lombok.Data;

@Data
public class RegisterRespDto {
    private String name;
    private String email;

    public static RegisterRespDto fromEntity(User user) {
        RegisterRespDto dto = new RegisterRespDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
