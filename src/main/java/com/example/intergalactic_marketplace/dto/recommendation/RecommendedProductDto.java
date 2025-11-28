package com.example.intergalactic_marketplace.dto.recommendation;

import com.example.intergalactic_marketplace.dto.product.ProductCategoryDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class RecommendedProductDto {
    String name;
    String sku;
    Double price;
    Set<ProductCategoryDto> categories;
}
