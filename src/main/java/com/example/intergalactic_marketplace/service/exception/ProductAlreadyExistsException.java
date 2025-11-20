package com.example.intergalactic_marketplace.service.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Product with name %s already exists";

    public ProductAlreadyExistsException(String productName) {
        super(String.format(MESSAGE, productName));
    }
}
