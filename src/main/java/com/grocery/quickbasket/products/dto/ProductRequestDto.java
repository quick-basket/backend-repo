package com.grocery.quickbasket.products.dto;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private List<MultipartFile> imageFiles;
    private List<Long> imagesToDelete;

}
