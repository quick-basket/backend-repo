package com.grocery.quickbasket.products.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grocery.quickbasket.products.dto.ProductRequestDto;
import com.grocery.quickbasket.products.dto.ProductResponseDto;
import com.grocery.quickbasket.products.service.ProductService;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct (
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") BigDecimal price,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam("quantity") Integer quantity,
        @RequestParam("storeId") Long storeId,
        @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {

    ProductRequestDto requestDto = new ProductRequestDto();
    requestDto.setName(name);
    requestDto.setDescription(description);
    requestDto.setPrice(price);
    requestDto.setCategoryId(categoryId);
    requestDto.setQuantity(quantity);
    requestDto.setStoreId(storeId);
    requestDto.setImageFiles(imageFiles);

    ProductResponseDto responseDto = productService.createProduct(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct (@PathVariable Long id, @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
