package com.example.intergalactic_marketplace.domain.recommendation;

import com.example.intergalactic_marketplace.domain.product.ProductCategory;
import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class RecommendedProduct {
    String name;
    String sku;
    Double price;
    Set<ProductCategory> categories;
}
