package com.example.intergalactic_marketplace.service.exception;

public class ProductNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Product with id %s not found";

    public ProductNotFoundException(String productId) {
        super(String.format(MESSAGE, productId));
    }
}
