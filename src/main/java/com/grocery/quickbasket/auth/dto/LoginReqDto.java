package com.grocery.quickbasket.auth.dto;

import lombok.Data;

@Data
public class LoginReqDto {
    private String email;
    private String password;
}
