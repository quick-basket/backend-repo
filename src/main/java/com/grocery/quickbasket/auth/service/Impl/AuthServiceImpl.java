package com.grocery.quickbasket.auth.service.Impl;

import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    @Override
    public String generateToken(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            return generateOAuth2Token(authentication);
        } else {
            return generateStandardToken(authentication);
        }
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

