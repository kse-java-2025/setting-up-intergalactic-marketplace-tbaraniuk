package com.example.intergalactic_marketplace.featuretoggle.annotation;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface DisabledFeatureToggle {
    FeatureToggles value();
}
