package com.example.intergalactic_marketplace.service.impl;

import com.example.intergalactic_marketplace.featuretoggle.FeatureFallbackBehavior;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggles;
import com.example.intergalactic_marketplace.featuretoggle.annotation.FeatureToggle;
import com.example.intergalactic_marketplace.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TranslationServiceImpl implements TranslationService {
    @Override
    @FeatureToggle(
            value = FeatureToggles.CONTENT_TRANSLATION,
            fallbackBehavior = FeatureFallbackBehavior.THROW_EXCEPTION
    )
    public String translateProduct(String productId, String language) {
        log.info("Translating product with id {} to language {}", productId, language);

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        return productId + " translated to " + language;
    }
}
