package com.example.intergalactic_marketplace.repository.projection;

import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;

import java.util.Set;

public interface ProductBasicProjection {
    String getName();

    String getSku();

    Double getPrice();

    Set<ProductCategoryEntity> getCategories();
}
