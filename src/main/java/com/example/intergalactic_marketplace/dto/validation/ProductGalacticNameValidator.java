package com.example.intergalactic_marketplace.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductGalacticNameValidator implements ConstraintValidator<ValidProductGalacticName, String> {
    private static final List<String> GALACTIC_KEYWORDS = List.of(
            "star", "galaxy", "comet", "nebula", "planet",
            "solar", "lunar", "cosmos", "cosmic", "celestial",
            "asteroid", "nova", "pulsar", "quasar"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String lowerCaseValue = value.toLowerCase();
        boolean hasKeyword = GALACTIC_KEYWORDS.stream()
                .anyMatch(lowerCaseValue::contains);

        if (!hasKeyword) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Name must include a galactic keyword (e.g., star, galaxy, comet)."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}