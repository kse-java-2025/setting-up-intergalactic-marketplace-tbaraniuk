package com.example.intergalactic_marketplace.service.mapper;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProducts;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientRequestDto;
import com.example.intergalactic_marketplace.dto.recommendation.RecommendationClientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RecommendationServiceMapper {
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "numberOfRecommendations", source = "numberOfRecommendations")
    RecommendationClientRequestDto toRecommendationClientRequestDto(UUID productId, int numberOfRecommendations);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "recommendedProducts", source = "recommendedProducts")
    RecommendedProducts toRecommendedProducts(RecommendationClientResponseDto recommendationClientResponseDto);
}
