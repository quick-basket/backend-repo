package com.grocery.quickbasket.auth.service;

import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    String register(RegisterReqDto registerReqDto);
    String generateToken(Authentication authentication);
    String verifyToken(String token);
    String addPassword(String email, PasswordReqDto passwordReqDto);
    String generateJwtSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
}
