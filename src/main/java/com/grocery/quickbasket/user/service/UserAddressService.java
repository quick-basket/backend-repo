package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.user.dto.UserAddressDto;

import java.util.List;

public interface UserAddressService {
    double getDistanceBetweenAddress(int address1, int address2);
    List<UserAddressDto> getUserAddresses();
    UserAddressDto createUserAddress(UserAddressDto userAddressDto);
    String deleteUserAddress(Long id);
    UserAddressDto updateUserAddress(UserAddressDto userAddressDto);
    UserAddressDto setPrimaryAddress(Long addressId);
    UserAddressDto getPrimaryAddress();
}
