package com.grocery.quickbasket.user.controller;

import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.user.dto.UpdateUserDto;
import com.grocery.quickbasket.user.dto.UserDto;
import com.grocery.quickbasket.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        return Response.successResponse("User Retrieve Success", userService.getUserProfile());
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto dto) {
        return Response.successResponse("User update success", userService.updateUserProfile(dto));
    }
}
