package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.exceptions.EmailAlreadyExistException;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.Role;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Log
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterRespDto register(RegisterReqDto registerReqDto) {
        if (userRepository.findByEmail(registerReqDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("email already exist");
        }
        if (!Objects.equals(registerReqDto.getPassword(), registerReqDto.getPasswordMatch())){
            throw new RuntimeException("Password is wrong");
        }

        User user = registerReqDto.toEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return RegisterRespDto.fromEntity(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email is not found"));
    }
}
