package com.grocery.quickbasket.user.controller;

import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.user.dto.UserAddressDto;
import com.grocery.quickbasket.user.entity.UserAddress;
import com.grocery.quickbasket.user.service.UserAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-address")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

//    @GetMapping()
//    public ResponseEntity<?> getDistance(@RequestBody int address1, @RequestBody int address2) {
//        return Response.successResponse("get distance", userAddressService.getDistanceBetweenAddress(address1, address2));
//    }

    @PostMapping()
    public ResponseEntity<?> addAddress(@RequestBody UserAddressDto dto) {
        return Response.successResponse("Add user address success", userAddressService.createUserAddress(dto));
    }

    @GetMapping()
    public ResponseEntity<?> getAllAddresses() {
        return Response.successResponse("Get all user addresses", userAddressService.getUserAddresses());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        return Response.successResponse("Delete address", userAddressService.deleteUserAddress(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody UserAddressDto dto) {
        return Response.successResponse("Update address", userAddressService.updateUserAddress(id, dto));
    }

    @PutMapping("/set-primary/{id}")
    public ResponseEntity<?> setPrimary(@PathVariable Long id) {
        return Response.successResponse("Set primary address", userAddressService.setPrimaryAddress(id));
    }


}
