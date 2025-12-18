package com.example.intergalactic_marketplace.dto.product;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductCategoryDto {
    UUID id;
    String name;
}
