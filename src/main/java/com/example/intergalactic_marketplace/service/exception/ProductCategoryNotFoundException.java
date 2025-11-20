package com.example.intergalactic_marketplace.service.exception;

public class ProductCategoryNotFoundException extends RuntimeException {
    public static final String ProductCategoryNotFound = "Product category with name %s not found";

    public ProductCategoryNotFoundException(String categoryName) {
        super(String.format(ProductCategoryNotFound, categoryName));
    }
}
