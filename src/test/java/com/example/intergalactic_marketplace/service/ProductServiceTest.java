package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.config.MappersTestConfiguration;
import com.example.intergalactic_marketplace.dto.product.BasicProductDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProductServiceImpl.class)
@Import(MappersTestConfiguration.class)
@DisplayName("Product Service Tests")
public class ProductServiceTest {
    private static final double PRODUCT_PRICE = 200;
    private static final String PRODUCT_DESCRIPTION = "This is a test product";

    @Autowired
    private ProductService productService;

    private static Stream<SaveProductDto> provideProducts() {
        return Stream.of(
                buildProduct("Galaxy Super Test"),
                buildProduct("Super Duper Star")
        );
    }

    private static SaveProductDto buildProduct(String name) {
        return SaveProductDto.builder().name(name).price(PRODUCT_PRICE).description(PRODUCT_DESCRIPTION).build();
    }

    @ParameterizedTest
    @MethodSource("provideProducts")
    void testAddProduct(SaveProductDto product) {
        BasicProductDto result = productService.createProduct(product);

        assertEquals(product.getName(), result.getName());
        assertEquals(PRODUCT_PRICE, result.getPrice());
        assertEquals(PRODUCT_DESCRIPTION, result.getDescription());
    }

    @Test
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<BasicProductDto> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
    }

    @Test
    void testUpdateProduct() {
        SaveProductDto product = buildProduct("Galaxy A46");

        BasicProductDto result = productService.createProduct(product);

        assertNotNull(result);

        SaveProductDto newProduct = buildProduct("Galaxy A47");

        BasicProductDto updatedProduct = productService.updateProduct(result.getUuid(), newProduct);

        assertNotNull(updatedProduct);
        assertEquals(result.getUuid(), updatedProduct.getUuid());
        assertEquals(newProduct.getName(), updatedProduct.getName());
        assertEquals(newProduct.getPrice(), updatedProduct.getPrice());
        assertEquals(newProduct.getDescription(), updatedProduct.getDescription());
    }

    @Test
    void testDeleteProduct() {
        SaveProductDto product = buildProduct("Galaxy A48");
        BasicProductDto result = productService.createProduct(product);

        productService.deleteProduct(result.getUuid());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(result.getUuid());
        });
    }
}
