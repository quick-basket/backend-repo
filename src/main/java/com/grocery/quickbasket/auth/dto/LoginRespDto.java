package com.grocery.quickbasket.auth.dto;

import lombok.Data;

@Data
public class LoginRespDto {
    private String message;
    private String token;
}
