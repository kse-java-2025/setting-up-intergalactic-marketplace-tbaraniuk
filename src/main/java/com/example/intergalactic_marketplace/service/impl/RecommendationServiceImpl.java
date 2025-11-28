package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientResponseDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggles;
import com.example.intergalactic_marketplace.featuretoggle.annotation.FeatureToggle;
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
import org.springframework.web.util.UriComponentsBuilder;

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
    @FeatureToggle(FeatureToggles.RECOMMENDATIONS)
    public RecommendedProductsDto getRecommendedProducts(String productId) {
        log.info("getRecommendedProducts: productId={}", productId);

        try {
            RecommendationClientResponseDto recommendationClientResponseDto = recommendationRestClient.get()
                    .uri(uriBuilder -> UriComponentsBuilder.fromUriString(recommendationServiceUrl)
                            .queryParam("productId", productId)
                            .queryParam("limit", DEFAULT_NUMBER_OF_RECOMMENDATIONS)
                            .build()
                            .toUri())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("Recommendation Server failed to fetch recommendations for productId={}. Response Code={}", productId, response.getStatusCode());
                        throw new RecommendedProductsRetrievalException(productId);
                    })
                    .body(RecommendationClientResponseDto.class);

            RecommendedProducts recommendedProducts = recommendationServiceMapper.toRecommendedProducts(recommendationClientResponseDto);

            return recommendationServiceMapper.toRecommendedProductsDto(recommendedProducts);
        } catch (Exception e) {
            log.error("getRecommendedProducts: failed to fetch recommendations for productId={}", productId, e);
            return RecommendedProductsDto.builder()
                    .recommendedProducts(List.of())
                    .build();
        }
    }
}
