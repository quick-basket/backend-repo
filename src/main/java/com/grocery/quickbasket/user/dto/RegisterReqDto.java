package com.grocery.quickbasket.user.dto;

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

    private String referralCode; 

}
