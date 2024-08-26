package com.grocery.quickbasket.auth.dto;

import lombok.Data;

@Data
public class PasswordReqDto {
    private String password;
    private String confirmPassword;
}
