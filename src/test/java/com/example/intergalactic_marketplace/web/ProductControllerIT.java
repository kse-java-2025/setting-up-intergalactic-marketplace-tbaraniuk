package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.CreateProductDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException.PRODUCT_ALREADY_EXISTS;
import static com.example.intergalactic_marketplace.web.GlobalExceptionHandler.VALIDATION_FAILED_MESSAGE;
import static org.mockito.Mockito.reset;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("ProductController Integration Tests")
@Tag("product-service")
public class ProductControllerIT {
    private static final CreateProductDto CRATE_PRODUCT_DTO = buildProduct("Test Galaxy Product");
    private static final CreateProductDto INVALID_PRODUCT_DTO = buildProduct("Test");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private ProductService productService;

    private static CreateProductDto buildProduct(String name) {
        return CreateProductDto.builder().name(name).price(100.0).description("Test product").build();
    }

    @BeforeEach
    void setUp() {
        reset(productService);
    }

    @Test
    @SneakyThrows
    void testAddProductWithSameName() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                CONFLICT,
                String.format(PRODUCT_ALREADY_EXISTS, CRATE_PRODUCT_DTO.getName())
        );
        problemDetail.setType(URI.create("product-already-exists"));
        problemDetail.setTitle("Product Already Exists");

        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                });

        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isConflict())
                .andExpect(content().json(objectMapper.writeValueAsString(problemDetail)));
    }

    @Test
    @SneakyThrows
    void testAddProductWithInvalidName() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, VALIDATION_FAILED_MESSAGE);
        problemDetail.setType(URI.create("validation-error"));
        problemDetail.setTitle("Validation Failed");

        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_PRODUCT_DTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("validation-error"))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.detail").value(VALIDATION_FAILED_MESSAGE));
    }
}
