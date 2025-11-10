package com.example.intergalactic_marketplace.dto.recommendation;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RecommendedProductsDto {
    List<RecommendedProductDto> recommendedProducts;
}
