package com.example.intergalactic_marketplace.domain.recommendation;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class RecommendedProducts {
    String productId;
    List<RecommendedProduct> recommendedProducts;
}
