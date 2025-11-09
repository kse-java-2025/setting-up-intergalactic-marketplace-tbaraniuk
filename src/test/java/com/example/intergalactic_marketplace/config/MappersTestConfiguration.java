package com.example.intergalactic_marketplace.config;

import com.example.intergalactic_marketplace.service.mapper.ProductMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MappersTestConfiguration {
    @Bean
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }
}
