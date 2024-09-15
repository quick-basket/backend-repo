package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.location.service.LocationService;
import com.grocery.quickbasket.user.dto.UserAddressDto;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.entity.UserAddress;
import com.grocery.quickbasket.user.repository.UserAddressRepository;
import com.grocery.quickbasket.user.service.UserAddressService;
import com.grocery.quickbasket.user.service.UserService;
import lombok.extern.java.Log;
import org.locationtech.jts.geom.Point;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class UserAddressImpl implements UserAddressService {
    private final UserAddressRepository addressRepository;
    private final LocationService locationService;
    private final UserService userService;

    public UserAddressImpl(UserAddressRepository addressRepository, @Lazy LocationService locationService, UserService userService) {
        this.addressRepository = addressRepository;
        this.locationService = locationService;
        this.userService = userService;
    }

    @Override
    public double getDistanceBetweenAddress(int address1, int address2) {
        return addressRepository.calculateDistance(address1, address2);
    }

    @Override
    public List<UserAddressDto> getUserAddresses() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        List<UserAddress> addressList = addressRepository.findByUserId(userId);
        log.info("addressList size: " + addressList.toString());
        return addressList.stream().map(UserAddressDto::fromEntity).toList();
    }

    @Override
    public UserAddressDto createUserAddress(UserAddressDto userAddressDto) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        User currUser = userService.findById(userId);
        UserAddress userAddress = UserAddressDto.toEntity(userAddressDto);
        userAddress.setUser(currUser);

        Point userLocation = locationService.createPoint(userAddressDto.getLongitude(), userAddressDto.getLatitude());
        userAddress.setLocation(userLocation);

        userAddress.setIsPrimary(!addressRepository.existsByUserId(userId));
        addressRepository.save(userAddress);

        return UserAddressDto.fromEntity(userAddress);
    }

    @Override
    public String deleteUserAddress(Long id) {
        UserAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Address not found with id: " + id));

        // If this is the primary address, we might want to handle that
        if (address.getIsPrimary()) {
            // Find another address for this user and make it primary
            addressRepository.findFirstByUserIdAndIdNot(address.getUser().getId(), id)
                    .ifPresent(newPrimary -> {
                        newPrimary.setIsPrimary(true);
                        addressRepository.save(newPrimary);
                    });
        }

        addressRepository.delete(address);
        return "Address successfully deleted";
    }

    @Override
    public UserAddressDto updateUserAddress(Long id, UserAddressDto userAddressDto) {
        UserAddress existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Address Not Found"));

        existingAddress.setAddress(userAddressDto.getAddress());
        existingAddress.setCity(userAddressDto.getCity());
        existingAddress.setPostalCode(userAddressDto.getPostalCode());
        existingAddress.setProvince(userAddressDto.getProvince());

        if (userAddressDto.getLongitude() != null && userAddressDto.getLatitude() != null){
            existingAddress.setLocation(
                    locationService.createPoint(userAddressDto.getLongitude(), userAddressDto.getLatitude())
            );
        }

        addressRepository.save(existingAddress);
        return UserAddressDto.fromEntity(existingAddress);
    }

    @Override
    public UserAddressDto setPrimaryAddress(Long addressId) {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");

        UserAddress newPrimaryAddress = addressRepository.findByIdAndUserId(addressId, userId);

        addressRepository.resetPrimaryForUser(userId);

        newPrimaryAddress.setIsPrimary(true);
        addressRepository.save(newPrimaryAddress);
        return UserAddressDto.fromEntity(newPrimaryAddress);
    }

    @Override
    public UserAddressDto getPrimaryAddress() {
        UserAddress primaryAddress = addressRepository.findByIsPrimaryIsTrue()
                .orElseThrow(() -> new DataNotFoundException("Primary Address Not Found"));

        return UserAddressDto.fromEntity(primaryAddress);
    }
}
