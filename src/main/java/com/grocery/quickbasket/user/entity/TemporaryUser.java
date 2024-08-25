package com.grocery.quickbasket.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
public class TemporaryUser implements Serializable {
    private String email;
    private String name;
    private String phone;
    private String verificationToken;
}
