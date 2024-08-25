package com.grocery.quickbasket.auth.dto;

import lombok.Data;

@Data
public class SocialLoginRespDto {
    private String status;
    private String token;
    private String emailExist;
}
