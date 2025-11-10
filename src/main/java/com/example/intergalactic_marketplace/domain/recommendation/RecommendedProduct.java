package com.example.intergalactic_marketplace.domain.recommendation;

import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class RecommendedProduct {
    UUID uuid;
    String name;
    Double price;
    Set<ProductCategory> categories;
}
