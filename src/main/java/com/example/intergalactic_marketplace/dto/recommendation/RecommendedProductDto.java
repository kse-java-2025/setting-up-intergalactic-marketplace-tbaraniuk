package com.example.intergalactic_marketplace.dto.recommendation;

import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RecommendedProductDto {
    UUID uuid;
    String name;
    String sku;
    Double price;
    Set<ProductCategoryDto> categories;
}
