package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;

import java.util.UUID;

public interface RecommendationService {
    RecommendedProductsDto getRecommendedProducts(UUID productId);
}
