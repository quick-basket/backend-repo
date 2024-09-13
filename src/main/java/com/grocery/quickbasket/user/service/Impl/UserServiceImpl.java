package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.cloudinary.service.CloudinaryService;
import com.grocery.quickbasket.exceptions.EmailNotExistException;
import com.grocery.quickbasket.exceptions.UserIdNotFoundException;
import com.grocery.quickbasket.user.dto.UpdateUserDto;
import com.grocery.quickbasket.user.dto.UserDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Log
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException("User not found"));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUserFromSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto) {
        User user = new User();
        user.setEmail(payloadSocialLoginReqDto.getEmail());
        user.setName(payloadSocialLoginReqDto.getName());
        user.setIsVerified(true);
        user.setImgProfile(payloadSocialLoginReqDto.getImageUrl());
        userRepository.save(user);

        return user;
    }

    @Override
    public boolean isUserSocialLogin(String email) {
        User user = findByEmail(email);
        return user != null && user.getPassword() == null && user.getIsVerified();
    }

    @Override
    public UserDto getUserProfile() {
        var claims = Claims.getClaimsFromJwt();
        log.info("CLAIMS " + claims.toString());
        String currentUserEmail = (String) claims.get("sub");

        User currentUser = findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new EmailNotExistException("user is not found");
        }
        return UserDto.fromUser(currentUser);
    }

    @Override
    public UserDto updateUserProfile(UpdateUserDto dto) {
        var claims = Claims.getClaimsFromJwt();
        String currentUserEmail = (String) claims.get("sub");

        User currentUser = findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new EmailNotExistException("user is not found");
        }
        currentUser.setName(dto.getName());
        currentUser.setPhone(dto.getPhoneNumber());
        userRepository.save(currentUser);
        return UserDto.fromUser(currentUser);
    }

    @Override
    public UserDto updateProfileImage(MultipartFile profileImage) {
        var claims = Claims.getClaimsFromJwt();
        String currentUserEmail = (String) claims.get("sub");

        User currentUser = findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new EmailNotExistException("user is not found");
        }

        // Upload profile image to Cloudinary
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadProfileUserImage(profileImage);
            currentUser.setImgProfile(imageUrl); // Update user's profile image URL
        } else {
            throw new IllegalArgumentException("Profile image is required");
        }

        userRepository.save(currentUser);
        return UserDto.fromUser(currentUser);
    }

    @Override
    public User getCurrentUser() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        return findById(userId);
    }

}
