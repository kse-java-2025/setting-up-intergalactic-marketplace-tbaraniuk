package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.dto.product.ProductBasicDto;
import com.example.intergalactic_marketplace.dto.product.SaveProductDto;
import com.example.intergalactic_marketplace.service.ProductService;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;

import static com.example.intergalactic_marketplace.web.GlobalExceptionHandler.VALIDATION_FAILED_MESSAGE;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.reset;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("ProductController Integration Tests")
@Tag("product-service")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductControllerIT {
    private static final SaveProductDto CRATE_PRODUCT_DTO = buildProduct("Cosmic Product");
    private static final SaveProductDto INVALID_PRODUCT_DTO = buildProduct("Test");

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance().options(wireMockConfig().dynamicHttpsPort()).configureStaticDsl(true).build();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private RecommendationService recommendationService;

    @MockitoSpyBean
    private ProductService productService;

    @DynamicPropertySource
    static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("application.recommendation-service.base-url", wireMockExtension::baseUrl);
        WireMock.configureFor(wireMockExtension.getPort());
    }

    private static SaveProductDto buildProduct(String name) {
        return SaveProductDto.builder().name(name).price(100.0).description("Test product").build();
    }

    @BeforeEach
    void setUp() {
        reset(productService, recommendationService);
    }

    @Test
    @SneakyThrows
    void testCreatingProduct() {
        MvcResult createResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andReturn();

        ProductBasicDto createdProduct = objectMapper.readValue(createResult.getResponse().getContentAsString(), ProductBasicDto.class);

        Assertions.assertNotNull(createdProduct.getUuid(), "UUID should not be null");

        stubFor(WireMock.get("/recommendation-service/v1/recommendations")
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
    void testAddProductWithSameName() {
        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                });
//
//        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(CRATE_PRODUCT_DTO))
//                )
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.type").value("product-already-exists"))
//                .andExpect(jsonPath("$.title").value("Product Already Exists"));
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
