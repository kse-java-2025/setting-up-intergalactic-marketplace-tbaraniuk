package com.example.intergalactic_marketplace.featuretoggle.aspect;

import com.example.intergalactic_marketplace.featuretoggle.FeatureToggleService;
import com.example.intergalactic_marketplace.featuretoggle.FeatureToggles;
import com.example.intergalactic_marketplace.featuretoggle.annotation.FeatureToggle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {
    private final FeatureToggleService featureToggleService;

    @Around(value = "@annotation(featureToggle)")
    public Object checkFeatureToggleAnnotation(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        FeatureToggles toggle = featureToggle.value();

        if (featureToggleService.check(toggle.getFeatureName())) {
            return joinPoint.proceed();
        }

        log.warn("checkToggle: feature {} is disabled", toggle.getFeatureName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();

        return determineFallback(returnType);
    }

    public Object determineFallback(Class<?> returnType) {
        if (returnType.isArray()) {
            return java.lang.reflect.Array.newInstance(returnType.getComponentType(), 0);
        } else if (List.class.isAssignableFrom(returnType)) {
            return List.of();
        } else if (Set.class.isAssignableFrom(returnType)) {
            return Set.of();
        } else if (Collection.class.isAssignableFrom(returnType)) {
            return List.of();
        } else if (returnType == void.class || returnType == Void.class) {
            return null;
        } else {
            try {
                return returnType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.warn("checkToggle: could not create empty instance for return type {}. Returning null.", returnType.getName());
                return null;
            }
        }
    }
}
