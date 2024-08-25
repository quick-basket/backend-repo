package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.exceptions.EmailNotExistException;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
