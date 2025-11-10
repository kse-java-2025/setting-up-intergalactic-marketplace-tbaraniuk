package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.product.BasicProductDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final RecommendationService recommendationService;
    private final ProductMapper productMapper;

    private final CopyOnWriteArrayList<Product> products = buildProductsMock();
    private final List<ProductCategory> categories = buildProductCategoriesMock();

    @Override
    public Page<BasicProductDto> getAllProducts(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int start = currentPage * pageSize;

        List<BasicProductDto> dtos;
        int totalProducts = products.size();

        if (totalProducts < start) {
            dtos = List.of();
        } else {
            int end = Math.min(start + pageSize, products.size());
            List<Product> productPageList = products.subList(start, end);

            dtos = productMapper.toProductDtoList(productPageList);
        }

        Page<BasicProductDto> productPage = new PageImpl<>(dtos, pageable, dtos.size());

        log.info("getAllProducts. Returning page {} of {} with {} products. Total products: {}",
                productPage.getNumber(), productPage.getTotalPages(),
                productPage.getNumberOfElements(), productPage.getTotalElements());

        return productPage;
    }

    @Override
    public BasicProductDto createProduct(SaveProductDto productDto) {
        Optional<Product> existingProduct = products.stream()
                .filter(item -> item.getName().equals(productDto.getName()))
                .findFirst();

        if (existingProduct.isPresent()) {
            log.error("createProduct: product with name {} already exists", productDto.getName());

            throw new ProductAlreadyExistsException(existingProduct.get().getName());
        }

        UUID productId = UUID.randomUUID();
        Product savedProduct = productMapper.toProduct(productId, productDto);
        products.add(savedProduct);

        log.info("createProduct: product={}", savedProduct);

        return productMapper.toProductDto(savedProduct);
    }

    @Override
    public ProductCategoryDto createProductCategory(SaveProductCategoryDto productCategoryDto) {
        UUID productCategoryId = UUID.randomUUID();
        ProductCategory savedProductCategory = productMapper.toProductCategory(productCategoryId, productCategoryDto);

        categories.add(savedProductCategory);

        return productMapper.toProductCategoryDto(savedProductCategory);
    }

    @Override
    public BasicProductDto getProduct(java.util.UUID productId) {
        Optional<Product> existingProduct = Optional.ofNullable(products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("getProduct: product with id {} not found", productId);
                    return new ProductNotFoundException(productId);
                }));

        log.info("getProduct: productId={}", productId);

        RecommendedProducts recommendedProducts = recommendationService.getRecommendedProducts(productId);

        return productMapper.toProductDetailDto(existingProduct.get(), recommendedProducts);
    }

    @Override
    public BasicProductDto updateProduct(UUID productId, SaveProductDto productDto) {
        Optional<Product> existingProduct = Optional.ofNullable(products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("updateProduct: product with id {} not found", productId);
                    return new ProductNotFoundException(productId);
                }));

        log.info("updateProduct: productId={}", productId);

        Product updatedProduct = productMapper.toProduct(productId, productDto);
        products.remove(existingProduct.get());
        products.add(updatedProduct);

        return productMapper.toProductDto(updatedProduct);
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
