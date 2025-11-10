package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientRequestDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientResponseDto;
import com.example.intergalactic_marketplace.service.RecommendationService;
import com.example.intergalactic_marketplace.service.exception.RecommendedProductsRetrievalException;
import com.example.intergalactic_marketplace.service.mapper.RecommendationServiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final RestClient recommendationRestClient;
    private final RecommendationServiceMapper recommendationServiceMapper;
    private final String recommendationServiceUrl;

    private final int DEFAULT_NUMBER_OF_RECOMMENDATIONS = 10;

    public RecommendationServiceImpl(
            @Qualifier("recommendationRestClient") RestClient recommendationRestClient,
            @Value("${application.recommendation-service.url}") String recommendationServiceUrl,
            RecommendationServiceMapper recommendationServiceMapper
    ) {
        this.recommendationRestClient = recommendationRestClient;
        this.recommendationServiceUrl = recommendationServiceUrl;
        this.recommendationServiceMapper = recommendationServiceMapper;
    }

    @Override
    public RecommendedProducts getRecommendedProducts(UUID productId) {
        log.info("getRecommendedProducts: productId={}", productId);
        RecommendationClientRequestDto recommendationClientRequestDto = recommendationServiceMapper.toRecommendationClientRequestDto(productId, DEFAULT_NUMBER_OF_RECOMMENDATIONS);

        try {
            RecommendationClientResponseDto recommendationClientResponseDto = recommendationRestClient.post()
                    .uri(recommendationServiceUrl)
                    .body(recommendationClientRequestDto)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Recommendation Server failed to fetch recommendations for productId={}. Response Code={}", productId, response.getStatusCode());
                        throw new RecommendedProductsRetrievalException(productId);
                    })
                    .body(RecommendationClientResponseDto.class);

            return recommendationServiceMapper.toRecommendedProducts(recommendationClientResponseDto);
        } catch (Exception e) {
            log.error("getRecommendedProducts: failed to fetch recommendations for productId={}", productId, e);
            return RecommendedProducts.builder()
                    .recommendedProducts(List.of())
                    .build();
        }
    }
}
