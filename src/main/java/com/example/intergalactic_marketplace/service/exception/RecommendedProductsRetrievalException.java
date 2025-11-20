package com.example.intergalactic_marketplace.service.exception;

import java.util.UUID;

public class RecommendedProductsRetrievalException extends RuntimeException {
    public static final String RECOMMENDED_PRODUCTS_RETRIEVAL_ERROR = "Error retrieving recommended products for product with id %s";

    public RecommendedProductsRetrievalException(UUID productId) {
        super(String.format(RECOMMENDED_PRODUCTS_RETRIEVAL_ERROR, productId));
    }
}
