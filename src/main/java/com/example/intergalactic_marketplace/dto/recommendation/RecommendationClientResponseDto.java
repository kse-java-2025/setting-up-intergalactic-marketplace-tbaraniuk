package com.example.intergalactic_marketplace.dto.recommendation;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RecommendationClientResponseDto {
    String productId;
    List<RecommendedProductDto> recommendedProducts;
}
