package com.grocery.quickbasket.auth.dto;

import lombok.Data;

@Data
public class PayloadSocialLoginReqDto {
    private String email;
    private String name;
    private String googleId;
    private String imageUrl;
}
