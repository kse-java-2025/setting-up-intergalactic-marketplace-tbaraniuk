package com.example.intergalactic_marketplace.repository;

import com.example.intergalactic_marketplace.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, UUID> {
    boolean existsByName(String name);
}
