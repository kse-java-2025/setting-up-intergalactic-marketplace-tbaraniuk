package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.product.BasicProductDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    Page<BasicProductDto> getAllProducts(Pageable pageable);

    BasicProductDto createProduct(SaveProductDto productDto);

    ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto);

    BasicProductDto getProduct(UUID productId);

    BasicProductDto updateProduct(UUID productId, SaveProductDto productDto);

    void deleteProduct(UUID productId);
}
