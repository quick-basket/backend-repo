package com.grocery.quickbasket.user.controller;

import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.user.service.UserAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/addresses")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping()
    public ResponseEntity<?> getDistance(@RequestBody int address1, @RequestBody int address2) {
        return Response.successResponse("get distance", userAddressService.getDistanceBetweenAddress(address1, address2));
    }
}
