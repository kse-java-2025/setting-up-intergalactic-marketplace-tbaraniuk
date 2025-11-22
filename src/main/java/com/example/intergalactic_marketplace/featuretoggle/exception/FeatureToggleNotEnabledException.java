package com.example.intergalactic_marketplace.featuretoggle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FeatureToggleNotEnabledException extends RuntimeException {
    public static final String FEATURE_TOGGLE_NOT_ENABLED = "Feature toggle %s is not enabled";

    public FeatureToggleNotEnabledException(String featureName) {
        super(String.format(FEATURE_TOGGLE_NOT_ENABLED, featureName));
    }
}
