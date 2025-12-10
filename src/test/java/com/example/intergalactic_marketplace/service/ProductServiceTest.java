package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.config.MappersTestConfiguration;
import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.service.exception.ProductCategoryNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductServiceImpl.class)
@Import(MappersTestConfiguration.class)
@DisplayName("Product Service Tests")
public class ProductServiceTest {
    private static final double PRODUCT_PRICE = 200;
    private static final String PRODUCT_DESCRIPTION = "This is a test product";
    private static final String PRODUCT_CATEGORY_NAME = "Electronics";

    @MockitoBean
    private RecommendationService recommendationService;

    @Autowired
    private ProductService productService;

    @Captor
    private ArgumentCaptor<UUID> recommendationServiceArgumentCaptor;

    private static Stream<SaveProductDto> provideProducts() {
        return Stream.of(
                buildProduct("Galaxy Super Test"),
                buildProduct("Super Duper Star")
        );
    }

    private static SaveProductDto buildProduct(String name) {
        return SaveProductDto.builder().name(name).price(PRODUCT_PRICE).description(PRODUCT_DESCRIPTION).build();
    }

    private static SaveProductDto buildProductWithCategories(String name, List<ProductCategoryDto> categories) {
        return SaveProductDto.builder().name(name).price(PRODUCT_PRICE).description(PRODUCT_DESCRIPTION).categories(categories).build();
    }

    private static SaveProductCategoryDto buildProductCategory(String productCategoryName) {
        return SaveProductCategoryDto.builder()
                .name(productCategoryName)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideProducts")
    @DisplayName("Should create a new product successfully")
    void testAddProduct(SaveProductDto product) {
        ProductBasicDto result = productService.createProduct(product);

        assertEquals(product.getName(), result.getName());
        assertEquals(PRODUCT_PRICE, result.getPrice());
        assertEquals(PRODUCT_DESCRIPTION, result.getDescription());
    }

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductBasicDto> oldPage = productService.getAllProducts(pageable);
        assertNotNull(oldPage);

        SaveProductDto product = buildProduct("Galaxy A44");
        ProductBasicDto newProduct = productService.createProduct(product);
        assertNotNull(newProduct);

        Page<ProductBasicDto> newPage = productService.getAllProducts(pageable);

        assertNotNull(newPage);
        assertEquals(1, newPage.getContent().size() - oldPage.getContent().size());
    }

    @Test
    @DisplayName("Should get a product successfully")
    void testGetProduct() {
        when(recommendationService.getRecommendedProducts(recommendationServiceArgumentCaptor.capture())).thenAnswer(
                inv -> buildRecommendedProductsMock()
        );

        SaveProductDto product = buildProduct("Galaxy A45");
        ProductBasicDto result = productService.createProduct(product);

        ProductBasicDto retrievedProduct = productService.getProduct(result.getUuid());

        assertNotNull(retrievedProduct);
        assertEquals(result.getUuid(), retrievedProduct.getUuid());
    }

    @Test
    @DisplayName("Should update a product successfully")
    void testUpdateProduct() {
        SaveProductDto product = buildProduct("Galaxy A46");

        ProductBasicDto result = productService.createProduct(product);

        assertNotNull(result);

        SaveProductDto newProduct = buildProduct("Galaxy A47");

        ProductBasicDto updatedProduct = productService.updateProduct(result.getUuid(), newProduct);

        assertNotNull(updatedProduct);
        assertEquals(result.getUuid(), updatedProduct.getUuid());
        assertEquals(newProduct.getName(), updatedProduct.getName());
        assertEquals(newProduct.getPrice(), updatedProduct.getPrice());
        assertEquals(newProduct.getDescription(), updatedProduct.getDescription());
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void testDeleteProduct() {
        SaveProductDto product = buildProduct("Galaxy A48");
        ProductBasicDto result = productService.createProduct(product);

        productService.deleteProduct(result.getUuid());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(result.getUuid());
        });
    }

    @Test
    @DisplayName("Should create a new product category successfully")
    void testCreateProductCategory() {
        SaveProductCategoryDto categoryDto = buildProductCategory(PRODUCT_CATEGORY_NAME);

        ProductCategoryDto result = productService.createProductCategory(categoryDto);

        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertEquals(PRODUCT_CATEGORY_NAME, result.getName());
    }

    @Test
    void testCreateProductWithCategories() {
        SaveProductCategoryDto categoryDto = buildProductCategory("Headphones");
        SaveProductCategoryDto categoryDto2 = buildProductCategory("Tablets");

        ProductCategoryDto category1 = productService.createProductCategory(categoryDto);
        ProductCategoryDto category2 = productService.createProductCategory(categoryDto2);

        SaveProductDto productDto = buildProductWithCategories("Test", List.of(category1, category2));

        ProductBasicDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals(2, result.getCategories().size());
    }

    @Test
    @DisplayName("Should throw ProductCategoryNotFoundException when creating a new product category with non-existent category")
    void testCreateProductWithNonExistentCategory() {
        ProductCategoryDto fakeCategory = ProductCategoryDto.builder()
                .uuid(UUID.randomUUID())
                .name("Non-Existent")
                .build();

        SaveProductDto productDto = buildProductWithCategories("Test Product With Non-Existent Category", List.of(fakeCategory));

        assertThrows(ProductCategoryNotFoundException.class, () -> {
            productService.createProduct(productDto);
        });
    }

    private RecommendedProductsDto buildRecommendedProductsMock() {
        return RecommendedProductsDto.builder()
                .recommendedProducts(List.of(
                        RecommendedProductDto.builder()
                                .uuid(UUID.randomUUID())
                                .name("Recommended Product 1")
                                .price(100.0)
                                .categories(Set.of())
                                .build(),
                        RecommendedProductDto.builder()
                                .uuid(UUID.randomUUID())
                                .name("Recommended Product 2")
                                .price(150.0)
                                .categories(Set.of())
                                .build()
                ))
                .build();
    }
}
