package com.example.intergalactic_marketplace.web.exception;

public class GreetingNotFoundException extends RuntimeException {
    public static final String GREETING_NOT_FOUND_MESSAGE = "Greeting with name %s not found";

    public GreetingNotFoundException(String name) {
        super(String.format(GREETING_NOT_FOUND_MESSAGE, name));
    }
}
