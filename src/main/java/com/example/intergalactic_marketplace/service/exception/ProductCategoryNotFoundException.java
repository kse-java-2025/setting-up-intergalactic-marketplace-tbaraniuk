package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class ProductCategoryNotFoundException extends RuntimeException {
    public static final String ProductCategoryNotFound = "Product category with id %s not found";

    public ProductCategoryNotFoundException(UUID categoryId) {
        super(String.format(ProductCategoryNotFound, categoryId));
    }
}
