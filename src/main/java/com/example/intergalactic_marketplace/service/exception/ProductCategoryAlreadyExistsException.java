package com.example.intergalactic_marketplace.service.exception;

public class ProductCategoryAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Product category with name %s already exists";

    public ProductCategoryAlreadyExistsException(String productCategoryName) {
        super(String.format(MESSAGE, productCategoryName));
    }
}
