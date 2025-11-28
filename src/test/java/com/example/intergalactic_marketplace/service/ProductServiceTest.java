package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.config.MappersTestConfiguration;
import com.example.intergalactic_marketplace.dto.product.ProductDetailDto;
import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;
import com.example.intergalactic_marketplace.entity.ProductEntity;
import com.example.intergalactic_marketplace.repository.ProductCategoryRepository;
import com.example.intergalactic_marketplace.repository.ProductRepository;
import com.example.intergalactic_marketplace.repository.projection.ProductBasicProjection;
import com.example.intergalactic_marketplace.service.exception.ProductCategoryNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.impl.ProductServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ProductServiceImpl.class)
@Import(MappersTestConfiguration.class)
@DisplayName("Product Service Tests")
public class ProductServiceTest {
    private static final double PRODUCT_PRICE = 200;
    private static final String PRODUCT_DESCRIPTION = "This is a test product";
    private static final String PRODUCT_CATEGORY_NAME = "Electronics";

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ProductCategoryRepository productCategoryRepository;

    @MockitoBean
    private RecommendationService recommendationService;

    @Autowired
    private ProductService productService;

    @Captor
    private ArgumentCaptor<String> recommendationServiceArgumentCaptor;

    private static SaveProductDto buildProduct(String name, String sku) {
        return SaveProductDto.builder().name(name).sku(sku).price(PRODUCT_PRICE).description(PRODUCT_DESCRIPTION).build();
    }

    private static SaveProductDto buildProductWithCategories(String name, String sku, List<UUID> categoryIds) {
        return SaveProductDto.builder().name(name).sku(sku).price(PRODUCT_PRICE).description(PRODUCT_DESCRIPTION).categoryIds(categoryIds).build();
    }

    private static SaveProductCategoryDto buildProductCategory(String productCategoryName) {
        return SaveProductCategoryDto.builder()
                .name(productCategoryName)
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("Should create a new product successfully")
    void testAddProduct() {
        SaveProductDto productDto = buildProduct("Galaxy A44", "galaxy-a44");

        UUID categoryId = UUID.randomUUID();
        String categoryName = "Headphones";

        ProductCategoryEntity categoryEntity = ProductCategoryEntity.builder().id(categoryId).name(categoryName).build();

        ProductEntity entity = ProductEntity.builder()
                .id(100L)
                .name(productDto.getName())
                .sku(productDto.getSku())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .categories(Set.of(categoryEntity))
                .build();

        when(productRepository.existsByName(productDto.getName())).thenReturn(false);
        when(productCategoryRepository.findAllById(List.of(categoryId))).thenReturn(List.of(categoryEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(entity);

        ProductDetailDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        assertEquals(productDto.getSku(), result.getSku());
        assertEquals(productDto.getPrice(), result.getPrice());
        assertEquals(productDto.getDescription(), result.getDescription());
        assertEquals(1, result.getCategories().size());
    }

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        ProductBasicProjection proj1 = mock(ProductBasicProjection.class);
        ProductBasicProjection proj2 = mock(ProductBasicProjection.class);

        Page<ProductBasicProjection> mockPage = new PageImpl<>(List.of(proj1, proj2));

        when(productRepository.findAllBy(pageable)).thenReturn(mockPage);

        Page<ProductBasicDto> result = productService.getAllProducts(pageable);

        verify(productRepository).findAllBy(pageable);
        assertEquals(2, result.getNumberOfElements());
    }

    @Test
    @DisplayName("Should return ALL products when query is empty")
    void shouldReturnAllProductsForEmptyQuery() {
        String emptyQuery = "    ";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductBasicProjection> mockPage = new PageImpl<>(List.of());

        when(productRepository.findAllBy(pageable)).thenReturn(mockPage);

        productService.searchProducts(emptyQuery, pageable);

        verify(productRepository).findAllBy(pageable);
        verify(productRepository, never()).searchByName(any(), any());
    }

    @Test
    @DisplayName("Should get a product successfully")
    void testGetProduct() {
        when(recommendationService.getRecommendedProducts(recommendationServiceArgumentCaptor.capture())).thenAnswer(
                inv -> buildRecommendedProductsMock()
        );

        SaveProductDto product = buildProduct("Galaxy A45", "galaxy-a45");
        ProductDetailDto result = productService.createProduct(product);

        ProductDetailDto retrievedProduct = productService.getProduct(result.getSku());

        assertNotNull(retrievedProduct);
        assertEquals(result.getName(), retrievedProduct.getName());
        assertEquals(result.getSku(), retrievedProduct.getSku());
        assertEquals(result.getPrice(), retrievedProduct.getPrice());
        assertEquals(result.getDescription(), retrievedProduct.getDescription());
        assertEquals(2, retrievedProduct.getRecommendedProducts().size());
        assertEquals(recommendationServiceArgumentCaptor.getValue(), retrievedProduct.getRecommendedProducts().get(0).getUuid());
        assertEquals(recommendationServiceArgumentCaptor.getValue(), retrievedProduct.getRecommendedProducts().get(1).getUuid());
    }

    @Test
    @DisplayName("Should update a product successfully")
    void testUpdateProduct() {
        SaveProductDto product = buildProduct("Galaxy A46", "galaxy-a46");

        ProductBasicDto result = productService.createProduct(product);

        assertNotNull(result);

        SaveProductDto newProduct = SaveProductDto.builder()
                .name("Galaxy A47")
                .sku("galaxy-a46")
                .price(150.0)
                .description("Updated description")
                .categoryIds(result.getCategories().stream().map(ProductCategoryDto::getId).toList())
                .build();

        ProductDetailDto updatedProduct = productService.updateProduct(result.getSku(), newProduct);

        assertNotNull(updatedProduct);
        assertEquals(newProduct.getName(), updatedProduct.getName());
        assertEquals(newProduct.getSku(), updatedProduct.getSku());
        assertEquals(newProduct.getPrice(), updatedProduct.getPrice());
        assertEquals(newProduct.getDescription(), updatedProduct.getDescription());
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void testDeleteProduct() {
        SaveProductDto product = buildProduct("Galaxy A48", "galaxy-a48");

        when(productRepository.existsByName(product.getName())).thenReturn(false);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(mock(ProductEntity.class));

        ProductBasicDto result = productService.createProduct(product);

        productService.deleteProduct(result.getSku());

        verify(productRepository).deleteByNaturalId(result.getSku());

        when(productRepository.findByNaturalId(result.getSku())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(result.getSku());
        });
    }

    @Test
    @DisplayName("Should create a new product category successfully")
    void testCreateProductCategory() {
        SaveProductCategoryDto categoryDto = buildProductCategory(PRODUCT_CATEGORY_NAME);

        ProductCategoryDto result = productService.createProductCategory(categoryDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(PRODUCT_CATEGORY_NAME, result.getName());
    }

    @Test
    void testCreateProductWithCategories() {
        SaveProductCategoryDto categoryDto = buildProductCategory("Headphones");
        SaveProductCategoryDto categoryDto2 = buildProductCategory("Tablets");

        ProductCategoryDto category1 = productService.createProductCategory(categoryDto);
        ProductCategoryDto category2 = productService.createProductCategory(categoryDto2);

        SaveProductDto productDto = buildProductWithCategories("Test", "test", List.of(category1.getId(), category2.getId()));

        ProductBasicDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals(2, result.getCategories().size());
    }

    @Test
    @DisplayName("Should throw ProductCategoryNotFoundException when creating a new product category with non-existent category")
    void testCreateProductWithNonExistentCategory() {
        UUID categoryId = UUID.randomUUID();

        ProductCategoryDto fakeCategory = ProductCategoryDto.builder()
                .id(categoryId)
                .name("Non-Existent")
                .build();

        SaveProductDto productDto = buildProductWithCategories("Test Product With Non-Existent Category", "test-product-with-non-existent-category", List.of(fakeCategory.getId()));

        when(productRepository.existsByName(productDto.getName())).thenReturn(false);
        when(productCategoryRepository.findAllById(List.of(categoryId))).thenReturn(List.of());

        assertThrows(ProductCategoryNotFoundException.class, () -> {
            productService.createProduct(productDto);
        });
    }

    private RecommendedProductsDto buildRecommendedProductsMock() {
        return RecommendedProductsDto.builder()
                .recommendedProducts(List.of(
                        RecommendedProductDto.builder()
                                .name("Recommended Product 1")
                                .sku("RP1")
                                .price(100.0)
                                .categories(Set.of())
                                .build(),
                        RecommendedProductDto.builder()
                                .name("Recommended Product 2")
                                .sku("RP2")
                                .price(150.0)
                                .categories(Set.of())
                                .build()
                ))
                .build();
    }
}
