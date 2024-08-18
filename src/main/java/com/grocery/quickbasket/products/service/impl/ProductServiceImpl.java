package com.grocery.quickbasket.products.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grocery.quickbasket.inventory.entity.Inventory;
import com.grocery.quickbasket.inventory.repository.InventoryRepository;
import com.grocery.quickbasket.productCategory.entity.ProductCategory;
import com.grocery.quickbasket.productCategory.repository.ProductCategoryRepository;
import com.grocery.quickbasket.productImages.entity.ProductImage;
import com.grocery.quickbasket.productImages.repository.ProductImageRepository;
import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;
import com.grocery.quickbasket.products.entity.Product;
import com.grocery.quickbasket.products.repository.ProductRepository;
import com.grocery.quickbasket.products.service.ProductService;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.repository.StoreRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.io.IOException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Cloudinary cloudinary;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;

    public ProductServiceImpl (ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, Cloudinary cloudinary, ProductImageRepository productImageRepository, InventoryRepository inventoryRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.cloudinary = cloudinary;
        this.productImageRepository = productImageRepository;
        this.inventoryRepository = inventoryRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        ProductCategory category = productCategoryRepository.findById(productRequestDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("category not found!"));
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        if (productRequestDto.getImageFiles() != null && !productRequestDto.getImageFiles().isEmpty()) {
            for (MultipartFile file : productRequestDto.getImageFiles()) {

                long maxFileSize = 2 * 1024 * 1024;
                if (file.getSize() > maxFileSize) {
                    throw new RuntimeException("File size exceeds the maximum limit of 2MB");
                }

                String contentType = file.getContentType();
                if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
                    throw new RuntimeException("Only JPG and PNG image types are allowed");
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

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(productRequestDto.getQuantity() != null ? productRequestDto.getQuantity() : 0);
        if (productRequestDto.getStoreId() != null) {
        Store store = storeRepository.findById(productRequestDto.getStoreId())
            .orElseThrow(() -> new RuntimeException("Store not found!"));
        inventory.setStore(store);
    } else {
        throw new RuntimeException("Store ID is required");
    }
        inventoryRepository.save(inventory);

        return mapToDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("product not found with id " + id));
        ProductCategory category = productCategoryRepository.findById(productRequestDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("category not found"));
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("product not found"));
        return mapToDto(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponseDto mapToDto (Product product ) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

}
