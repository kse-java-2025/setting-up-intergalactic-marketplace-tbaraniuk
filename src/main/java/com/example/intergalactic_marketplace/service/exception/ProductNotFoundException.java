package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Product with id %s not found";

    public ProductNotFoundException(UUID productId) {
        super(String.format(MESSAGE, productId));
    }
}
