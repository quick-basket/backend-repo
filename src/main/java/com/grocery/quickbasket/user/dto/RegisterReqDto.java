package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterReqDto {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Phone cannot be empty")
    private String phone;

    public User toEntity(){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
