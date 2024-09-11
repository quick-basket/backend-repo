package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.user.entity.UserAddress;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

@Data
public class UserAddressDto {
    private Long id;
    private Long userId;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Boolean isPrimary;

    public static UserAddressDto fromEntity(UserAddress userAddress) {
        UserAddressDto dto = new UserAddressDto();
        BeanUtils.copyProperties(userAddress, dto);
        dto.setUserId(userAddress.getUser().getId());
        Optional.ofNullable(userAddress.getLocation()).ifPresent(location -> {
            dto.setLongitude(location.getX());
            dto.setLatitude(location.getY());
        });
        return dto;
    }

    public static UserAddress toEntity(UserAddressDto dto) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(dto, userAddress);
        return userAddress;
    }
}
