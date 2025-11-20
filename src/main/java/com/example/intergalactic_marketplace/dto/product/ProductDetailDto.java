package com.example.intergalactic_marketplace.dto.product;

import com.example.intergalactic_marketplace.domain.recommendation.RecommendedProduct;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@SuperBuilder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class ProductDetailDto extends ProductBasicDto {
    List<RecommendedProduct> recommendedProducts;
}
