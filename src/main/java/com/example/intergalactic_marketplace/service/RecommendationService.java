package com.example.intergalactic_marketplace.service;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;

import java.util.UUID;

public interface RecommendationService {
    RecommendedProducts getRecommendedProducts(UUID productId);
}
