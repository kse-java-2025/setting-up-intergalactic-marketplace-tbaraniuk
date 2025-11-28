package com.example.intergalactic_marketplace.service.exception;

import java.util.List;
import java.util.UUID;

public class ProductCategoryNotFoundException extends RuntimeException {
    public static final String ProductCategoryNotFound = "Product category with id %s not found";
    public static final String ProductCategoriesNotFound = "Product categories with ids %s not found";

    public ProductCategoryNotFoundException(UUID categoryId) {
        super(String.format(ProductCategoryNotFound, categoryId));
    }

    public ProductCategoryNotFoundException(List<UUID> categoryIds) {
        super(String.format(ProductCategoriesNotFound, categoryIds));
    }
}
