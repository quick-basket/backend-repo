package com.grocery.quickbasket.auth.controller;

import com.grocery.quickbasket.auth.dto.LoginReqDto;
import com.grocery.quickbasket.auth.dto.LoginRespDto;
import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.repository.AuthRedisRepository;
import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
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

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
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

    @GetMapping("/verify/registration")
    public ResponseEntity<?> verify (@RequestParam("token") String token) {
        return Response.successResponse("Verification successfully", authService.verifyCode(token, AuthRedisRepository.REGISTRATION_PREFIX));
    }

    @GetMapping("/password-reset/verify")
    public ResponseEntity<?> verifyResetPassword (@RequestParam("token") String token) {
        return Response.successResponse("Verification successfully", authService.verifyCode(token, AuthRedisRepository.RESET_PREFIX));
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

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword (@RequestBody PasswordReqDto dto) {
        return Response.successResponse("Reset password successfully", authService.resetPassword(dto));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> passwordResetRequest (@RequestParam("email") String email) {
        return Response.successResponse("Check your email for verification link", authService.checkUserResetPassword(email));
    }

}


