package com.grocery.quickbasket.auth.controller;

import com.grocery.quickbasket.auth.dto.LoginReqDto;
import com.grocery.quickbasket.auth.dto.LoginRespDto;
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
        return Response.successResponse("Registration is successfully", userService.register(registerReqDto));
    }

}


