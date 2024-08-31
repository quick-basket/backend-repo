package com.grocery.quickbasket.user.dto;

import com.grocery.quickbasket.user.entity.User;
import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String email;
    private String image;
    private String phone;
    private boolean isVerified;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setImage(user.getImgProfile());
        userDto.setPhone(user.getPhone());
        userDto.setVerified(user.getIsVerified());
        return userDto;
    }
}
