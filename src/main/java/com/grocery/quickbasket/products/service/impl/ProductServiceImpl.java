package com.grocery.quickbasket.products.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import com.grocery.quickbasket.productCategory.repository.ProductCategoryRepository;
import com.grocery.quickbasket.productImages.entity.ProductImage;
import com.grocery.quickbasket.productImages.repository.ProductImageRepository;
import com.grocery.quickbasket.products.dto.ProductListResponseDto;
import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.products.service.ProductService;

import java.time.Instant;
import java.util.Map;
import java.io.IOException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Cloudinary cloudinary;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;

    public ProductServiceImpl (ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, Cloudinary cloudinary, ProductImageRepository productImageRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.cloudinary = cloudinary;
        this.productImageRepository = productImageRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if(productRepository.existsByName(productRequestDto.getName())) {
            throw new DataNotFoundException("product name already exists");
        }
        ProductCategory category = productCategoryRepository.findById(productRequestDto.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("category not found!"));
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(category);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        Product savedProduct = productRepository.save(product);

        if (productRequestDto.getImageFiles() != null && !productRequestDto.getImageFiles().isEmpty()) {
            for (MultipartFile file : productRequestDto.getImageFiles()) {

                long maxFileSize = 1 * 1024 * 1024;
                if (file.getSize() > maxFileSize) {
                    throw new DataNotFoundException("File size exceeds the maximum limit of 1MB");
                }

                String contentType = file.getContentType();
                if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
                    throw new DataNotFoundException("Only JPG and PNG image types are allowed");
                }
                try {
                    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    String imageUrl = (String) uploadResult.get("url");

                    ProductImage image = new ProductImage();
                    image.setProduct(savedProduct);
                    image.setImageUrl(imageUrl);
                    productImageRepository.save(image);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image to Cloudinary", e);
                }
            }
        }
        return ProductResponseDto.mapToDto(savedProduct);
    }

    @Transactional
    @Override
    public String updateProduct(Long id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("product not found with id " + id));
        ProductCategory category = productCategoryRepository.findById(productRequestDto.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("category not found"));
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(category);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);

        List<Long> imagesToDelete = productRequestDto.getImagesToDelete();
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            List<ProductImage> imagesToDeleteList = productImageRepository.findAllById(imagesToDelete);
            productImageRepository.deleteAll(imagesToDeleteList);
        }

        if (productRequestDto.getImageFiles() != null && !productRequestDto.getImageFiles().isEmpty()) {
            for (MultipartFile file : productRequestDto.getImageFiles()) {
                long maxFileSize = 1 * 1024 * 1024; // 1MB
                if (file.getSize() > maxFileSize) {
                    throw new DataNotFoundException("File size exceeds the maximum limit of 1MB");
                }

                String contentType = file.getContentType();
                if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
                    throw new DataNotFoundException("Only JPG and PNG image types are allowed");
                }

                try {
                    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    String imageUrl = (String) uploadResult.get("url");

                    // Save new image to database
                    ProductImage image = new ProductImage();
                    image.setProduct(product);
                    image.setImageUrl(imageUrl);
                    productImageRepository.save(image);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image to Cloudinary", e);
                }
            }
        }
        return "update successfully";
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("product not found"));
        
        List<String> imageUrls = productImageRepository.findByProduct(product)
            .stream().map(ProductImage::getImageUrl)
            .collect(Collectors.toList());
        List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
        int totalQuantity = inventories.stream()
            .mapToInt(Inventory::getQuantity)
            .sum();
        ProductResponseDto responseDto = ProductResponseDto.mapToDto(product);
        responseDto.setImageUrls(imageUrls);
        responseDto.setQuantity(totalQuantity);
        return responseDto;
    }

    @Override
    public List<ProductListResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductListResponseDto> responseDtos = new ArrayList<>();

        for (Product product : products) {
            ProductListResponseDto dto = new ProductListResponseDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());

            List<ProductImage> images = productImageRepository.findByProduct(product);
            List<String> imageUrls = images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
            List<Long> imageIds = images.stream()
                .map(ProductImage::getId)
                .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
            dto.setImageIds(imageIds);

            List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
            int totalQuantity = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
            dto.setQuantity(totalQuantity);
            responseDtos.add(dto);
        }
        return responseDtos;
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}