package com.example.intergalactic_marketplace.service.exception;

public class PersistenceException extends RuntimeException {
    public PersistenceException(Throwable ex) {
        super(ex);
    }
}
