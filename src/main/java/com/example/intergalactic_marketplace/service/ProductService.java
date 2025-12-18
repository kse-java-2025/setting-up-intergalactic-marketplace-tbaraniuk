package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.product.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    Page<ProductBasicDto> getAllProducts(Pageable pageable);

    Page<ProductBasicDto> searchProducts(String name, Pageable pageable);

    ProductDetailDto createProduct(SaveProductDto productDto);

    ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto);

    ProductCategoryDto updateProductCategory(UUID productCategoryId, SaveProductCategoryDto productCategoryDto);

    ProductDetailDto getProduct(String productId);

    ProductDetailDto updateProduct(String productId, SaveProductDto productDto);

    void deleteProduct(String productId);
}
