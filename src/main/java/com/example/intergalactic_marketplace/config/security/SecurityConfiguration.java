package com.example.intergalactic_marketplace.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
    private static final String API_V1_PRODUCTS = "/api/v1/products/**";
    private static final String API_V1_GREETINGS = "/api/v1/greetings/**";
    
    @Bean
    @Profile("test")
    public DefaultSecurityFilterChain testFilterChainProducts(HttpSecurity http) throws Exception {
        http
                .securityMatcher(API_V1_PRODUCTS)
                .cors(withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    @Bean
    @Order(1)
    @Profile("!test")
    public SecurityFilterChain filterChainProducts(HttpSecurity http) throws Exception {
        http
                .securityMatcher(API_V1_PRODUCTS)
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.GET, API_V1_PRODUCTS).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));

        return http.build();
    }

    @Bean
    @Order(2)
    @Profile("!test")
    public SecurityFilterChain filterChainGreetings(HttpSecurity http) throws Exception {
        http
                .securityMatcher(API_V1_GREETINGS)
                .cors(withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
