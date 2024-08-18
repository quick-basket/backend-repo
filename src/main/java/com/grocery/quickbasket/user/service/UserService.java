package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.User;

public interface UserService {
    RegisterRespDto register(RegisterReqDto registerReqDto);
    User findByEmail(String email);
}
