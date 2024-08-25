package com.grocery.quickbasket.auth.service.Impl;

import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.exceptions.EmailAlreadyExistException;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.entity.TemporaryUser;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.service.TemporaryUserService;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final TemporaryUserService temporaryUserService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserService userService, TemporaryUserService temporaryUserService, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.temporaryUserService = temporaryUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String register(RegisterReqDto registerReqDto) {
        if (userService.findByEmail(registerReqDto.getEmail()) != null) {
            throw new EmailAlreadyExistException("email already exist");
        }

        String verificationToken = UUID.randomUUID().toString();

        TemporaryUser temporaryUser = new TemporaryUser(
                registerReqDto.getEmail(),
                registerReqDto.getName(),
                registerReqDto.getPhone(),
                verificationToken
        );
        log.info(temporaryUser.toString());

        temporaryUserService.saveTemporaryUser(temporaryUser);

        return "Registration successful! = " + verificationToken;
    }

    @Override
    public String generateToken(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            return generateOAuth2Token(authentication);
        } else {
            return generateStandardToken(authentication);
        }
    }

    @Override
    public String verifyToken(String token) {
        TemporaryUser temporaryUser = temporaryUserService.getTemporaryUser(token);

        if (temporaryUser == null) {
            return "Invalid or expired verification Link";
        }

        User user = new User();
        user.setEmail(temporaryUser.getEmail());
        user.setName(temporaryUser.getName());
        user.setPhone(temporaryUser.getPhone());

        userService.save(user);

        temporaryUserService.deleteTemporaryUser(token);

        return "Email verified successfully! Please set your password to complete the registration.";
    }

    @Override
    public String addPassword(String email, PasswordReqDto passwordReqDto) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return "User not found";
        }

        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userService.save(user);

        return "Password added successfully";

    }

    @Override
    public String generateJwtSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto) {
        Instant now = Instant.now();
        User user = userService.findByEmail(payloadSocialLoginReqDto.getEmail());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("quick-basket")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("scope", user.getRole().name())
                .claim("userId", user.getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    private String generateOAuth2Token(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        log.info("PRINCIPAL" + oAuth2User.toString());

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            email = oAuth2User.getAttribute("notification_email");
        }

        if (email == null) {
            throw new RuntimeException("Email not found in OAuth2User attributes");
        }

        Long userId = userService.findByEmail(email).getId();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("quick-basket")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(email)  // Use email as the subject
                .claim("scope", scope)
                .claim("userId", userId)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateStandardToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Long userId = userService.findByEmail(authentication.getName()).getId();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("quick-basket")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())  // Use username as the subject
                .claim("scope", scope)
                .claim("userId", userId)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

