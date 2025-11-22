package com.example.intergalactic_marketplace.featuretoggle;

import lombok.Getter;

@Getter
public enum FeatureToggles {
    RECOMMENDATIONS("recommendations"),
    SOME_OTHER_FEATURE("other");

    private final String featureName;

    FeatureToggles(String featureName) {
        this.featureName = featureName;
    }
}
