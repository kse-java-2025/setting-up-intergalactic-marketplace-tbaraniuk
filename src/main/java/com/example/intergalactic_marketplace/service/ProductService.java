package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.product.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    Page<ProductBasicDto> getAllProducts(Pageable pageable);

    Page<ProductBasicDto> searchProducts(String name, Pageable pageable);

    ProductBasicDto createProduct(SaveProductDto productDto);

    ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto);

    ProductCategoryDto updateProductCategory(UUID productCategoryId, SaveProductCategoryDto productCategoryDto);

    ProductBasicDto getProduct(UUID productId);

    ProductBasicDto updateProduct(UUID productId, SaveProductDto productDto);

    void deleteProduct(UUID productId);
}
