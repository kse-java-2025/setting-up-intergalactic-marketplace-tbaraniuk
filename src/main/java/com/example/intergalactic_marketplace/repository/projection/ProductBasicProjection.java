package com.example.intergalactic_marketplace.repository.projection;

import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;

import java.util.Set;
import java.util.UUID;

public interface ProductBasicProjection {
    UUID getUuid();

    String getName();

    String getSku();

    Double getPrice();

    Set<ProductCategoryEntity> getCategories();
}
