package com.grocery.quickbasket.auth.controller;

import com.grocery.quickbasket.auth.dto.LoginReqDto;
import com.grocery.quickbasket.auth.dto.LoginRespDto;
import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.email.service.EmailService;
import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
@Log
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginReqDto.getEmail(),
                        loginReqDto.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = authService.generateToken(authentication);

        LoginRespDto loginRespDto = new LoginRespDto();
        loginRespDto.setToken(token);
        loginRespDto.setMessage("Login successful");

        Cookie cookie = new Cookie("sid", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(12 * 60 * 60); // 1 hour
        cookie.setSecure(false); // Set to true if using HTTPS

        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(loginRespDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sid".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.ok().body("Logout Successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterReqDto registerReqDto) {
        return Response.successResponse("Registration is successfully", authService.register(registerReqDto));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify (@RequestParam("token") String token) {
        return Response.successResponse("Verification successfully", authService.verifyCode(token));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail (@RequestParam("email") String email) {
        return Response.successResponse("Email is exist", userService.existsByEmail(email));
    }

    @PostMapping("/generate-token")
    public ResponseEntity<?> exchangeToken (@RequestBody PayloadSocialLoginReqDto dto) {
        return Response.successResponse("Generated JWT Token", authService.generateJwtSocialLogin(dto));
    }

    @PostMapping("/google-signin")
    public ResponseEntity<?> googleSignin (@RequestBody PayloadSocialLoginReqDto dto) {
        return Response.successResponse("Login successfully", authService.googleSignIn(dto));
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword (@RequestBody PasswordReqDto dto) {
        return Response.successResponse("Set password successfull", authService.addPassword(dto));
    }

}


