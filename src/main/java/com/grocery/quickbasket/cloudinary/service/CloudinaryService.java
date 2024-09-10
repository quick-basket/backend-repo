package com.grocery.quickbasket.cloudinary.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadProfileUserImage(MultipartFile file);
}
