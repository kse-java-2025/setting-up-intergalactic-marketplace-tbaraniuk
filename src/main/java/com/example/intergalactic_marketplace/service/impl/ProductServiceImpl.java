package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductCategoryAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductCategoryNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final RecommendationService recommendationService;
    private final ProductMapper productMapper;

    private final CopyOnWriteArrayList<Product> products = buildProductsMock();
    private final List<ProductCategory> categories = buildProductCategoriesMock();

    @Override
    public Page<ProductBasicDto> getAllProducts(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int start = currentPage * pageSize;

        List<ProductBasicDto> dtos;
        int totalProducts = products.size();

        if (totalProducts < start) {
            dtos = List.of();
        } else {
            int end = Math.min(start + pageSize, products.size());
            List<Product> productPageList = products.subList(start, end);

            dtos = productPageList.stream()
                    .map(product -> {
                        Set<ProductCategoryDto> categoryDtos = getCategoriesByIds(product.getCategories());
                        return productMapper.toProductDto(product, categoryDtos);
                    })
                    .collect(Collectors.toList());
        }

        Page<ProductBasicDto> productPage = new PageImpl<>(dtos, pageable, totalProducts);

        log.info("getAllProducts. Returning page {} of {} with {} products. Total products: {}",
                productPage.getNumber(), productPage.getTotalPages(),
                productPage.getNumberOfElements(), productPage.getTotalElements());

        return productPage;
    }

    @Override
    public ProductBasicDto createProduct(SaveProductDto productDto) {
        Optional<Product> existingProduct = products.stream()
                .filter(item -> item.getName().equals(productDto.getName()))
                .findFirst();

        if (existingProduct.isPresent()) {
            log.error("createProduct: product with name {} already exists", productDto.getName());

            throw new ProductAlreadyExistsException(existingProduct.get().getName());
        }

        Set<UUID> categoryIds = resolveCategoriesIds(productDto.getCategories());

        UUID productId = UUID.randomUUID();
        Product savedProduct = productMapper.toProduct(productId, productDto, categoryIds);
        products.add(savedProduct);

        log.info("createProduct: product={}", savedProduct);

        return productMapper.toProductDto(savedProduct, getCategoriesByIds(categoryIds));
    }

    @Override
    public ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto) {
        UUID productCategoryId = UUID.randomUUID();
        ProductCategory savedProductCategory = productMapper.toProductCategory(productCategoryId, productCategoryDto);

        boolean exists = categories.stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(productCategoryDto.getName()));

        if (exists) {
            throw new ProductCategoryAlreadyExistsException(productCategoryDto.getName());
        }

        categories.add(savedProductCategory);

        return productMapper.toProductCategoryDto(savedProductCategory);
    }

    @Override
    public ProductCategoryDto updateProductCategory(UUID productCategoryId, SaveProductCategoryDto productCategoryDto) {
        Optional<ProductCategory> existingProductCategory = categories.stream()
                .filter(item -> item.getUuid().equals(productCategoryId))
                .findFirst();

        if (existingProductCategory.isPresent()) {
            ProductCategory updatedProductCategory = productMapper.toProductCategory(
                    existingProductCategory.get().getUuid(), productCategoryDto);

            categories.remove(existingProductCategory.get());
            categories.add(updatedProductCategory);

            return productMapper.toProductCategoryDto(updatedProductCategory);
        }

        throw new ProductCategoryNotFoundException(productCategoryDto.getName());
    }

    @Override
    public ProductBasicDto getProduct(java.util.UUID productId) {
        Optional<Product> existingProduct = Optional.ofNullable(products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("getProduct: product with id {} not found", productId);
                    return new ProductNotFoundException(productId);
                }));

        log.info("getProduct: productId={}", productId);

        RecommendedProductsDto recommendedProductsDto = recommendationService.getRecommendedProducts(productId);

        Set<ProductCategoryDto> categoryDtos = getCategoriesByIds(existingProduct.get().getCategories());

        return productMapper.toProductDetailDto(existingProduct.get(), categoryDtos, recommendedProductsDto);
    }

    @Override
    public ProductBasicDto updateProduct(UUID productId, SaveProductDto productDto) {
        Optional<Product> existingProduct = Optional.ofNullable(products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("updateProduct: product with id {} not found", productId);
                    return new ProductNotFoundException(productId);
                }));

        log.info("updateProduct: productId={}", productId);

        Set<UUID> categoryIds = resolveCategoriesIds(productDto.getCategories());

        Product updatedProduct = productMapper.toProduct(productId, productDto, categoryIds);
        products.remove(existingProduct.get());
        products.add(updatedProduct);

        return productMapper.toProductDto(updatedProduct, getCategoriesByIds(categoryIds));
    }

    @Override
    public void deleteProduct(UUID productId) {
        Optional<Product> existingProduct = products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst();

        log.info("deleteProduct: productId={}", productId);

        if (existingProduct.isPresent()) {
            products.remove(existingProduct.get());
            log.info("deleteProduct: product removed");
        }
    }

    private Set<UUID> resolveCategoriesIds(List<ProductCategoryDto> productCategoryDtos) {
        if (productCategoryDtos == null || productCategoryDtos.isEmpty()) {
            return Set.of();
        }

        return productCategoryDtos.stream()
                .map(dto -> {
                    categories.stream()
                            .filter(cat -> cat.getUuid().equals(dto.getUuid()))
                            .findFirst()
                            .orElseThrow(() -> new ProductCategoryNotFoundException(dto.getName()));
                    return dto.getUuid();
                })
                .collect(Collectors.toSet());
    }

    private Set<ProductCategoryDto> getCategoriesByIds(Set<UUID> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Set.of();
        }

        return categoryIds.stream()
                .map(id -> categories.stream()
                        .filter(cat -> cat.getUuid().equals(id))
                        .findFirst()
                        .map(productMapper::toProductCategoryDto)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private CopyOnWriteArrayList<ProductCategory> buildProductCategoriesMock() {
        return new CopyOnWriteArrayList<>(
                List.of(
                        ProductCategory.builder()
                                .uuid(UUID.randomUUID())
                                .name("Laptops")
                                .build(),
                        ProductCategory.builder()
                                .uuid(UUID.randomUUID())
                                .name("Mobiles")
                                .build()
                )
        );
    }

    private CopyOnWriteArrayList<Product> buildProductsMock() {
        return new CopyOnWriteArrayList<>(
                List.of(
                        Product.builder()
                                .uuid(UUID.randomUUID())
                                .name("Super Star Laptop")
                                .description("An awesome super star laptop")
                                .price(100.0)
                                .build(),
                        Product.builder()
                                .uuid(UUID.randomUUID())
                                .name("Galaxy Note 10")
                                .price(50.5)
                                .build(),
                        Product.builder()
                                .uuid(UUID.randomUUID())
                                .name("Comet Mobile")
                                .price(60.5)
                                .build()
                )
        );
    }
}
