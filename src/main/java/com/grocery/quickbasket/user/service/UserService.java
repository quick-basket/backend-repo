package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.User;

public interface UserService {
    User findByEmail(String email);
    void save(User user);
    boolean existsByEmail(String email);
    User saveUserFromSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
    boolean isUserSocialLogin(String email);
}
