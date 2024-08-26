package com.grocery.quickbasket.auth.service.Impl;

import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.dto.SocialLoginRespDto;
import com.grocery.quickbasket.auth.repository.AuthRedisRepository;
import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.email.service.EmailService;
import com.grocery.quickbasket.exceptions.EmailAlreadyExistException;
import com.grocery.quickbasket.exceptions.PasswordNotMatchException;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.service.TemporaryUserService;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final TemporaryUserService temporaryUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuthRedisRepository authRedisRepository;
    private final EmailService emailService;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserService userService, TemporaryUserService temporaryUserService, PasswordEncoder passwordEncoder, AuthRedisRepository authRedisRepository, EmailService emailService) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.temporaryUserService = temporaryUserService;
        this.passwordEncoder = passwordEncoder;
        this.authRedisRepository = authRedisRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public RegisterRespDto register(RegisterReqDto registerReqDto) {
        if (userService.findByEmail(registerReqDto.getEmail()) != null) {
            if (userService.isUserSocialLogin(registerReqDto.getEmail())) {
                log.info("EMAIL IS SOCIAL LOGIN" + registerReqDto.getEmail());
                throw new EmailAlreadyExistException("This email is already registered with a social account. Please log in using your social account.");
            }
            throw new EmailAlreadyExistException("This email is already registered");
        }

        User newUser = new User();
        newUser.setEmail(registerReqDto.getEmail());
        newUser.setName(registerReqDto.getName());
        newUser.setPhone(registerReqDto.getPhone());
        newUser.setIsVerified(false);
        userService.save(newUser);

        sendVerificationEmail(newUser);

        RegisterRespDto respDto = new RegisterRespDto();
        respDto.setEmail(newUser.getEmail());
        respDto.setName(newUser.getName());
        return respDto;
    }

    @Override
    public String generateToken(Authentication authentication) {
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

    @Override
    public boolean verifyCode(String code) {
        String email = authRedisRepository.getEmail(code);

        return email != null;
    }

    @Override
    public String addPassword(PasswordReqDto passwordReqDto) {
        String email = authRedisRepository.getEmail(passwordReqDto.getVerificationCode());
        log.info("EMAIL" + email);

        if (!Objects.equals(passwordReqDto.getPassword(), passwordReqDto.getConfirmPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }
        log.info(passwordReqDto.toString());
        User user = userService.findByEmail(email);
        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userService.save(user);

        authRedisRepository.deleteVerificationToken(passwordReqDto.getVerificationCode());

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

    @Override
    public SocialLoginRespDto googleSignIn(PayloadSocialLoginReqDto payloadSocialLoginReqDto) {
        boolean exist = userService.existsByEmail(payloadSocialLoginReqDto.getEmail());

        SocialLoginRespDto socialLoginRespDto = new SocialLoginRespDto();
        if (exist) {
            String token = generateJwtSocialLogin(payloadSocialLoginReqDto);
            socialLoginRespDto.setToken(token);
            socialLoginRespDto.setStatus("success");
        } else {
            User newUser = userService.saveUserFromSocialLogin(payloadSocialLoginReqDto);
            String token = generateJwtSocialLogin(payloadSocialLoginReqDto);
            socialLoginRespDto.setStatus("new_user");
            socialLoginRespDto.setToken(token);
        }

        return socialLoginRespDto;
    }

    @Override
    public void sendVerificationEmail(User user) {
        String verificationCode = UUID.randomUUID().toString();
        String verificationLink = "http://localhost:3000/verify?code=" + verificationCode;

        authRedisRepository.saveVerificationToken(user.getEmail(), verificationCode);
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
    }
}

