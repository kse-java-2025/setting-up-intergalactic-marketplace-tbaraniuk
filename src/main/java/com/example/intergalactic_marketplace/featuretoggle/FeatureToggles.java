package com.example.intergalactic_marketplace.featuretoggle;

import lombok.Getter;

@Getter
public enum FeatureToggles {
    RECOMMENDATIONS("recommendations"),
    CONTENT_TRANSLATION("content_translation"),
    SOME_OTHER_FEATURE("other");

    private final String featureName;

    FeatureToggles(String featureName) {
        this.featureName = featureName;
    }
}
