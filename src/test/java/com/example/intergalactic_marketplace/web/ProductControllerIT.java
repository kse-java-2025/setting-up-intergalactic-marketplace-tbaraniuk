package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductCategoryDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientResponseDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductDto;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggleExtension;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggles;
import com.example.intergalactic_marketplace.featuretoggle.annotation.DisabledFeatureToggle;
import com.example.intergalactic_marketplace.featuretoggle.annotation.EnabledFeatureToggle;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.example.intergalactic_marketplace.featuretoggle.exception.FeatureToggleNotEnabledException.FEATURE_TOGGLE_NOT_ENABLED;
import static com.example.intergalactic_marketplace.web.GlobalExceptionHandler.VALIDATION_FAILED_MESSAGE;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.reset;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("ProductController Integration Tests")
@Tag("product-service")
@ExtendWith(FeatureToggleExtension.class)
public class ProductControllerIT extends AbstractIT {
    private static final SaveProductDto CRATE_PRODUCT_DTO = buildProduct("Cosmic Product", "cosmic-product");
    private static final SaveProductDto INVALID_PRODUCT_DTO = buildProduct("Test", "test");
    private static final SaveProductCategoryDto CREATE_PRODUCT_CATEGORY_DTO = buildProductCategory("Test Product");
    private static final RecommendedProductDto RECOMMENDED_PRODUCT_DTO = buildRecommendedProduct("Recommended Product", "recommended-product");
    private static final String TRANSLATION_LANGUAGE = "uk";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private RecommendationService recommendationService;

    @MockitoSpyBean
    private ProductService productService;

    private static SaveProductDto buildProduct(String name, String sku) {
        return SaveProductDto.builder().name(name).sku(sku).price(100.0).description("Test product").build();
    }

    private static SaveProductDto buildProductWithCategories(String name, String sku, List<UUID> categoryIds) {
        return SaveProductDto.builder().name(name).sku(sku).price(100.0).description("Test product").categoryIds(categoryIds).build();
    }

    private static SaveProductCategoryDto buildProductCategory(String productCategoryName) {
        return SaveProductCategoryDto.builder()
                .name(productCategoryName)
                .build();
    }

    private static RecommendedProductDto buildRecommendedProduct(String productName, String sku) {
        return RecommendedProductDto.builder()
                .name(productName)
                .sku(sku)
                .price(100.0)
                .build();
    }

    @BeforeEach
    void setUp() {
        reset(productService, recommendationService);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should handle ProductNotFoundException")
    void testProductNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/products/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("product-not-found"))
                .andExpect(jsonPath("$.title").value("Product Not Found"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should create a new product successfully")
    void testCreatingProduct() {
        MvcResult createResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createResult.getResponse().getContentAsString(), ProductBasicDto.class);

        Assertions.assertNotNull(createdProduct.getUuid(), "UUID should not be null");

        stubFor(WireMock.get(urlPathEqualTo("/recommendation-service/v1/recommendations"))
                .willReturn(aResponse().withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(createdProduct.getUuid()))));

        mockMvc.perform(get("/api/v1/products/{id}", createdProduct.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(CRATE_PRODUCT_DTO.getName()))
                .andExpect(jsonPath("$.price").value(CRATE_PRODUCT_DTO.getPrice()))
                .andExpect(jsonPath("$.description").value(CRATE_PRODUCT_DTO.getDescription()));
    }

    @Test
    @SneakyThrows
    @DisabledFeatureToggle(FeatureToggles.RECOMMENDATIONS)
    @DisplayName("Should return recommended products with feature disabled")
    void testGetProductRecommendationsDisabled() {
        MvcResult createResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createResult.getResponse().getContentAsString(), ProductBasicDto.class);

        Assertions.assertNotNull(createdProduct.getUuid(), "UUID should not be null");

        RecommendationClientResponseDto mockRecommendationResponseDto = RecommendationClientResponseDto.builder()
                .productId(createdProduct.getUuid())
                .recommendedProducts(List.of(RECOMMENDED_PRODUCT_DTO))
                .build();

        stubFor(WireMock.get(urlPathEqualTo("/recommendation-service/v1/recommendations"))
                .withQueryParam("productId", equalTo(createdProduct.getUuid().toString()))
                .willReturn(aResponse().withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockRecommendationResponseDto))));

        mockMvc.perform(get("/api/v1/products/{id}", createdProduct.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(CRATE_PRODUCT_DTO.getName()))
                .andExpect(jsonPath("$.price").value(CRATE_PRODUCT_DTO.getPrice()))
                .andExpect(jsonPath("$.description").value(CRATE_PRODUCT_DTO.getDescription()))
                .andExpect(jsonPath("$.recommendedProducts").isEmpty());
    }

    @Test
    @SneakyThrows
    @EnabledFeatureToggle(FeatureToggles.RECOMMENDATIONS)
    @DisplayName("Should return recommended products with feature enabled")
    void testGetProductRecommendationsEnabled() {
        MvcResult createResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createResult.getResponse().getContentAsString(), ProductBasicDto.class);

        Assertions.assertNotNull(createdProduct.getUuid(), "UUID should not be null");

        RecommendationClientResponseDto mockRecommendationResponseDto = RecommendationClientResponseDto.builder()
                .productId(createdProduct.getUuid())
                .recommendedProducts(List.of(RECOMMENDED_PRODUCT_DTO))
                .build();

        stubFor(WireMock.get(urlPathEqualTo("/recommendation-service/v1/recommendations"))
                .withQueryParam("productId", equalTo(createdProduct.getUuid().toString()))
                .willReturn(aResponse().withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockRecommendationResponseDto))));

        mockMvc.perform(get("/api/v1/products/{id}", createdProduct.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(CRATE_PRODUCT_DTO.getName()))
                .andExpect(jsonPath("$.price").value(CRATE_PRODUCT_DTO.getPrice()))
                .andExpect(jsonPath("$.description").value(CRATE_PRODUCT_DTO.getDescription()))
                .andExpect(jsonPath("$.recommendedProducts").isNotEmpty())
                .andExpect(jsonPath("$.recommendedProducts[0].name").value(RECOMMENDED_PRODUCT_DTO.getName()))
                .andExpect(jsonPath("$.recommendedProducts[0].price").value(RECOMMENDED_PRODUCT_DTO.getPrice()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should not create a new product with same name")
    void testAddProductWithSameName() {
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
                .andExpect(jsonPath("$.type").value("product-already-exists"))
                .andExpect(jsonPath("$.title").value("Product Already Exists"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should not create a new product with invalid name")
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
                .andExpect(jsonPath("$.detail").value(problemDetail.getDetail()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should update a product successfully")
    void testUpdateProductWithCategories() {
        String OLD_PRODUCT_NAME = "Old Star Product Name";
        String OLD_PRODUCT_SKU = "old-star-product-sku";
        String NEW_PRODUCT_NAME = "New Star Product Name";
        Double NEW_PRODUCT_PRICE = 10.0;
        String NEW_PRODUCT_DESCRIPTION = "New Product Description";

        ProductCategoryDto category1 = createCategory("Category 1");
        ProductCategoryDto category2 = createCategory("Category 2");
        ProductCategoryDto category3 = createCategory("Category 3");

        SaveProductDto product = buildProductWithCategories(OLD_PRODUCT_NAME, OLD_PRODUCT_SKU, List.of(category1.getId(), category2.getId()));

        MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.sku").value(product.getSku()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.categories", hasSize(2)))
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createResult.getResponse().getContentAsString(), ProductBasicDto.class);

        SaveProductDto updatedProduct = SaveProductDto.builder()
                .name(NEW_PRODUCT_NAME)
                .price(NEW_PRODUCT_PRICE)
                .sku(product.getSku())
                .categoryIds(List.of(category3.getId()))
                .description(NEW_PRODUCT_DESCRIPTION)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/{productId}", createdProduct.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedProduct.getName()))
                .andExpect(jsonPath("$.sku").value(updatedProduct.getSku()))
                .andExpect(jsonPath("$.price").value(updatedProduct.getPrice()))
                .andExpect(jsonPath("$.categories", hasSize(1)))
                .andExpect(jsonPath("$.categories[0].name").value(category3.getName()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should update a product successfully")
    void testDeleteNonExistentProduct() {
        mockMvc.perform(delete("/api/v1/products/{id}", java.util.UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should delete existing product")
    void testDeleteExistingProduct() {
        MvcResult createResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductBasicDto.class
        );

        mockMvc.perform(delete("/api/v1/products/{id}", createdProduct.getUuid()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", createdProduct.getUuid()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should create product with categories and retrieve with categories")
    void testCreateProductCategoryAndRetrieve() {
        ProductCategoryDto productCategory = createCategory(CREATE_PRODUCT_CATEGORY_DTO.getName());

        SaveProductDto product = buildProductWithCategories("Galaxy A48", "galaxy-a48", List.of(productCategory.getId()));

        MvcResult createProductResult = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categories", hasSize(1)))
                .andExpect(jsonPath("$.categories[0].name").value(CREATE_PRODUCT_CATEGORY_DTO.getName()))
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), ProductBasicDto.class);

        stubFor(WireMock.get(urlPathEqualTo("/recommendation-service/v1/recommendations")).willReturn(aResponse().withStatus(OK.value()).withBody(objectMapper.writeValueAsString(createdProduct.getUuid()))));

        mockMvc.perform(get("/api/v1/products/{id}", createdProduct.getUuid())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories", hasSize(1)))
                .andExpect(jsonPath("$.categories[0].name").value(CREATE_PRODUCT_CATEGORY_DTO.getName()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should fail when creating duplicate category")
    void testCreateDuplicateCategory() {
        String categoryName = "Duplicate Category";
        createCategory(categoryName);

        SaveProductCategoryDto duplicateCategory = SaveProductCategoryDto.builder()
                .name(categoryName)
                .build();

        mockMvc.perform(post("/api/v1/products/productCategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCategory)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("product-category-already-exists"))
                .andExpect(jsonPath("$.title").value("Product Category Already Exists"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should update a product category successfully")
    void testUpdateProductCategory() {
        ProductCategoryDto category = createCategory("Category 1");

        SaveProductCategoryDto updatedCategoryDto = SaveProductCategoryDto.builder()
                .name("Updated Category")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/productCategory/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedCategoryDto.getName()));
    }

    @Test
    @SneakyThrows
    @DisabledFeatureToggle(FeatureToggles.CONTENT_TRANSLATION)
    @DisplayName("Should fail return product translation on feature off")
    void testTranslationFeatureDisabled() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, String.format(FEATURE_TOGGLE_NOT_ENABLED, FeatureToggles.CONTENT_TRANSLATION.name().toLowerCase()));
        problemDetail.setType(URI.create("feature-toggle-not-enabled"));
        problemDetail.setTitle("Feature Toggle Not Enabled");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                }).andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductBasicDto.class);

        mockMvc.perform(post("/api/v1/products/{id}/translate", createdProduct.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRANSLATION_LANGUAGE))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.type").value(problemDetail.getType().toString()))
                .andExpect(jsonPath("$.title").value(problemDetail.getTitle()))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.detail").value(problemDetail.getDetail()));
    }

    @Test
    @SneakyThrows
    @EnabledFeatureToggle(FeatureToggles.CONTENT_TRANSLATION)
    @DisplayName("Should return product translation on feature on")
    void testTranslationFeatureEnabled() {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                }).andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductBasicDto.class);

        mockMvc.perform(post("/api/v1/products/{id}/translate", createdProduct.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TRANSLATION_LANGUAGE))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    private ProductCategoryDto createCategory(String name) {
        SaveProductCategoryDto dto = SaveProductCategoryDto.builder()
                .name(name)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/products/productCategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProductCategoryDto.class
        );
    }
}
