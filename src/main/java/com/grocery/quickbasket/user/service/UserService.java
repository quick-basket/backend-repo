package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.user.entity.User;

public interface UserService {
    User findByEmail(String email);
}
