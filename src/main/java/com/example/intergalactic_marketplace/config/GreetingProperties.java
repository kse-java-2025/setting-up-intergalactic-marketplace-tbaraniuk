package com.example.intergalactic_marketplace.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {
    Map<String, Greeting> greetings;

    @Data
    @NoArgsConstructor
    public static class Greeting {
        String name;
        String message;
    }
}
