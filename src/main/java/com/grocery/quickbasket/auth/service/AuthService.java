package com.grocery.quickbasket.auth.service;

import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.dto.SocialLoginRespDto;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthService {
    RegisterRespDto register(RegisterReqDto registerReqDto);
    String generateToken(Authentication authentication);
    boolean verifyCode(String code);
    String addPassword(PasswordReqDto passwordReqDto);
    String generateJwtSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
    SocialLoginRespDto googleSignIn(PayloadSocialLoginReqDto payloadSocialLoginReqDto);
    void sendVerificationEmail(User user);
}
