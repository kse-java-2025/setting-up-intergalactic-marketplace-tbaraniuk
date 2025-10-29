package com.example.intergalactic_marketplace.web.exception;

public class GreetingNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Greeting with name %s not found";

    public GreetingNotFoundException(String name) {
        super(String.format(MESSAGE, name));
    }
}
