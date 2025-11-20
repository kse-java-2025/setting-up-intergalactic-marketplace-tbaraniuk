package com.example.intergalactic_marketplace.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ProductGalacticNameValidator.class)
@Documented
public @interface ValidProductGalacticName {
    String message() default "Invalid name: the name should contain at least one of the following keywords: star, galaxy, comet, nebula, planet, solar, lunar, cosmos, cosmic, celestial, asteroid, nova, pulsar, quasar";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
