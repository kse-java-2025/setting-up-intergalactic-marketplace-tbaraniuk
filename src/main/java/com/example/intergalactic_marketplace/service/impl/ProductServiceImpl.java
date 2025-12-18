package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.dto.product.*;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;
import com.example.intergalactic_marketplace.entity.ProductEntity;
import com.example.intergalactic_marketplace.repository.ProductCategoryRepository;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.projection.ProductBasicProjection;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.example.intergalactic_marketplace.service.exception.PersistenceException;
import com.example.intergalactic_marketplace.service.exception.ProductCategoryNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final RecommendationService recommendationService;
    private final ProductMapper productMapper;

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBasicDto> getAllProducts(Pageable pageable) {
        Page<ProductBasicProjection> productPage = productRepository.findAllBy(pageable);

        Page<ProductBasicDto> resultPage = productPage.map(productMapper::toProductBasicDto);

        log.info("getAllProducts. Returning page {} of {} with {} products. Total products: {}",
                productPage.getNumber(), productPage.getTotalPages(),
                productPage.getNumberOfElements(), productPage.getTotalElements());

        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBasicDto> searchProducts(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return getAllProducts(pageable);
        }

        Page<ProductBasicProjection> productPage = productRepository.searchByName(name, pageable);

        Page<ProductBasicDto> resultPage = productPage.map(productMapper::toProductBasicDto);

        log.info("searchProducts. Returning page {} of {} with {} products. Total products: {}",
                productPage.getNumber(), productPage.getTotalPages(),
                productPage.getNumberOfElements(), productPage.getTotalElements());

        return resultPage;
    }

    @Override
    @Transactional
    public ProductDetailDto createProduct(SaveProductDto productDto) {
        List<UUID> categoryIds = productDto.getCategoryIds();

        if (categoryIds == null || categoryIds.isEmpty()) {
            categoryIds = List.of();
        }

        List<ProductCategoryEntity> categoryEntities = productCategoryRepository.findAllById(categoryIds);

        if (categoryEntities.size() != categoryIds.size()) {
            Set<UUID> found = categoryEntities.stream()
                    .map(ProductCategoryEntity::getId)
                    .collect(Collectors.toSet());

            List<UUID> missing = categoryIds.stream()
                    .filter(id -> !found.contains(id))
                    .toList();

            throw new ProductCategoryNotFoundException(missing);
        }

        ProductEntity productToSave = productMapper.toProductEntity(productDto);

        productToSave.setCategories(new HashSet<>(categoryEntities));

        ProductEntity savedProduct = productRepository.save(productToSave);

        log.info("createProduct: product={}", savedProduct);

        return productMapper.toProductDetailDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto) {
        ProductCategoryEntity productCategoryToSave = productMapper.toProductCategoryEntity(productCategoryDto);

        ProductCategoryEntity savedProductCategory = productCategoryRepository.save(productCategoryToSave);

        return productMapper.toProductCategoryDto(savedProductCategory);
    }

    @Override
    @Transactional
    public ProductCategoryDto updateProductCategory(UUID productCategoryId, SaveProductCategoryDto productCategoryDto) {
        Optional<ProductCategoryEntity> existingProductCategory = productCategoryRepository
                .findById(productCategoryId);

        if (existingProductCategory.isEmpty()) {
            log.warn("updateProductCategory: product category with id {} not found", productCategoryId);

            throw new ProductCategoryNotFoundException(productCategoryId);
        }

        productMapper.updateProductEntityFromDto(productCategoryDto, existingProductCategory.get());

        ProductCategoryEntity updatedProductCategory = productCategoryRepository.save(existingProductCategory.get());

        return productMapper.toProductCategoryDto(updatedProductCategory);

    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDto getProduct(String productId) {
        Optional<ProductEntity> product = productRepository.findByNaturalId(productId);

        if (product.isEmpty()) {
            throw new ProductNotFoundException(productId);
        }

        log.info("getProduct: productId={}", productId);

        RecommendedProductsDto recommendedProductsDto = recommendationService.getRecommendedProducts(productId);

        return productMapper.toProductDetailDto(product.get(), recommendedProductsDto);
    }

    @Override
    @Transactional
    public ProductDetailDto updateProduct(String productId, SaveProductDto productDto) {
        Optional<ProductEntity> existingProduct = productRepository.findByNaturalId(productId);

        if (existingProduct.isEmpty()) {
            log.error("updateProduct: product with id {} not found", productId);

            throw new ProductNotFoundException(productId);
        }

        productMapper.updateProductEntityFromDto(productDto, existingProduct.get());

        List<UUID> newCategoryIds = productDto.getCategoryIds();

        if (newCategoryIds != null) {
            List<ProductCategoryEntity> newCategories = productCategoryRepository.findAllById(newCategoryIds);

            if (newCategories.size() != newCategoryIds.size()) {
                throw new IllegalArgumentException("One or more categories not found");
            }

            if (existingProduct.get().getCategories() == null) {
                existingProduct.get().setCategories(new HashSet<>());
            }

            existingProduct.get().getCategories().clear();
            existingProduct.get().getCategories().addAll(newCategories);
        }

        log.info("updateProduct: productId={}", productId);

        ProductEntity savedProduct = productRepository.save(existingProduct.get());

        return productMapper.toProductDetailDto(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId) {
        try {
            productRepository.deleteByNaturalId(productId);
        } catch (Exception ex) {
            log.error("deleteProduct: product with id {} not found", productId, ex);

            throw new PersistenceException(ex);
        }
    }
}
