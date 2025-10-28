package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.product.Product;
import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.dto.product.ProductDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    private CopyOnWriteArrayList<Product> products = buildProductsMock();

    @Override
    public List<ProductDto> getAllProducts() {
        log.info("getAllProducts. Products length: {}", products.size());
        return productMapper.toProductDtoList(products);
    }

    @Override
    public ProductDto createProduct(CreateProductDto productDto) {
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
    public ProductDto getProduct(java.util.UUID productId) {
        Optional<Product> existingProduct = Optional.ofNullable(products.stream()
                .filter(item -> item.getUuid().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("getProduct: product with id {} not found", productId);
                    return new ProductNotFoundException(productId);
                }));

        log.info("getProduct: productId={}", productId);

        return productMapper.toProductDto(existingProduct.get());
    }

    @Override
    public ProductDto updateProduct(UUID productId, CreateProductDto productDto) {
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
