package com.example.intergalactic_marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;


@Slf4j
@Configuration
public class RestClientConfiguration {
    private final int responseTimeout;

    public RestClientConfiguration(@Value("${application.restclient.response-timeout:1000}") int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    @Bean("recommendationRestClient")
    public RestClient recommendationRestClient() {
        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory(responseTimeout))
                .build();
    }

    public ClientHttpRequestFactory getClientHttpRequestFactory(int responseTimeout) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults().withReadTimeout(Duration.ofMillis(responseTimeout));
        return ClientHttpRequestFactoryBuilder.detect().build(settings);
    }
}
