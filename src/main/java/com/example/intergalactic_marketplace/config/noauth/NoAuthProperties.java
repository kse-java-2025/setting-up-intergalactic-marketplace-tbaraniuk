package com.example.intergalactic_marketplace.config.noauth;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Data
@NoArgsConstructor
@Profile("no-auth")
@ConfigurationProperties(prefix = "auth")
public class NoAuthProperties {
    Map<String, Object> claims;
}
