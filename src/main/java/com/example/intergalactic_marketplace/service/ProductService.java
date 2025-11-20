package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDto> getAllProducts();

    ProductDto createProduct(CreateProductDto productDto);

    ProductDto getProduct(UUID productId);

    ProductDto updateProduct(UUID productId, CreateProductDto productDto);

    void deleteProduct(UUID productId);
}
