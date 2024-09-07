package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.user.repository.UserAddressRepository;
import com.grocery.quickbasket.user.service.UserAddressService;
import org.springframework.stereotype.Service;

@Service
public class UserAddressImpl implements UserAddressService {
    private final UserAddressRepository addressRepository;

    public UserAddressImpl(UserAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public double getDistanceBetweenAddress(int address1, int address2) {
        return addressRepository.calculateDistance(address1, address2);
    }
}
