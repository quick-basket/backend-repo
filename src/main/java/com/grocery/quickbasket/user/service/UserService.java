package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.dto.StoreAdminRequesetDto;
import com.grocery.quickbasket.user.dto.UpdateUserDto;
import com.grocery.quickbasket.user.dto.UpdateUserRoleRequest;
import com.grocery.quickbasket.user.dto.UserDto;
import com.grocery.quickbasket.user.dto.UserRoleResponseDto;
import com.grocery.quickbasket.user.entity.User;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    User findByEmail(String email);
    User findById(Long id);
    void save(User user);
    boolean existsByEmail(String email);
    User saveUserFromSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
    boolean isUserSocialLogin(String email);
    UserDto getUserProfile();
    UserDto updateUserProfile(UpdateUserDto dto);
    UserDto updateProfileImage(MultipartFile profileImage);
    User getCurrentUser();
    UserRoleResponseDto createUserRole(UpdateUserRoleRequest request);
    UserRoleResponseDto updateUserRole(Long storeAdminId, StoreAdminRequesetDto requesetDto);
    List<User> getAllUsersNotInStoreAdmins();
}
