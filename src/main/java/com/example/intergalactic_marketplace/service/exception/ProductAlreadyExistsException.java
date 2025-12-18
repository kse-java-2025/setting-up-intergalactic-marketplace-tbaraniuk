package com.example.intergalactic_marketplace.service.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public static final String PRODUCT_ALREADY_EXISTS = "Product with name %s already exists";

    public ProductAlreadyExistsException(String productName) {
        super(String.format(PRODUCT_ALREADY_EXISTS, productName));
    }
}
