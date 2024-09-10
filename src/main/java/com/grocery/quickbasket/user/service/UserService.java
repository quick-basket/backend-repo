package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.dto.UpdateUserDto;
import com.grocery.quickbasket.user.dto.UserDto;
import com.grocery.quickbasket.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User findByEmail(String email);
    void save(User user);
    boolean existsByEmail(String email);
    User saveUserFromSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
    boolean isUserSocialLogin(String email);
    UserDto getUserProfile();
    UserDto updateUserProfile(UpdateUserDto dto);
    UserDto updateProfileImage(MultipartFile profileImage);
}
