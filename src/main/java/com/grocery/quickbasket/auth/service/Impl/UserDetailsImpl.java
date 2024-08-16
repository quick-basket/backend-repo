package com.grocery.quickbasket.auth.service.Impl;

import com.grocery.quickbasket.auth.entity.UserAuth;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log
public class UserDetailsImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userService.findByEmail(email);
        return new UserAuth(user);
    }
}
