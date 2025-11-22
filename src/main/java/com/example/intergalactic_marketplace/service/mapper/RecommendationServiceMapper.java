package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientResponseDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendedProductsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecommendationServiceMapper {
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "recommendedProducts", source = "recommendedProducts")
    RecommendedProducts toRecommendedProducts(RecommendationClientResponseDto recommendationClientResponseDto);

    @Mapping(target = "recommendedProducts", source = "recommendedProducts")
    RecommendedProductsDto toRecommendedProductsDto(RecommendedProducts recommendedProducts);
}
