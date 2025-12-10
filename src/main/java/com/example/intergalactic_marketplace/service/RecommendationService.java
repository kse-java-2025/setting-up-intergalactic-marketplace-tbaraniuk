package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;

public interface RecommendationService {
    RecommendedProductsDto getRecommendedProducts(String productId);
}
