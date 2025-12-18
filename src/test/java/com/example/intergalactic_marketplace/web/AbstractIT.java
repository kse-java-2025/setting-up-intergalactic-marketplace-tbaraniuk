package com.example.intergalactic_marketplace.web;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.TimeZone;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Testcontainers
public abstract class AbstractIT {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> PostgresContainer = new PostgreSQLContainer<>("postgres:13.2-alpine");

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    @DynamicPropertySource
    static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("application.recommendation-service.base-path", wireMockExtension::baseUrl);
        registry.add("application.recommendation-service.url",
                () -> wireMockExtension.baseUrl() + "/recommendation-service/v1/recommendations");
    }

    @BeforeAll
    static void configureWireMock() {
        WireMock.configureFor(wireMockExtension.getPort());
    }
}
