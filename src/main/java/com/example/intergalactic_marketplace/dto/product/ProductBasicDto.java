package com.example.intergalactic_marketplace.dto.product;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode
@AllArgsConstructor
public class ProductBasicDto {
    UUID uuid;
    String sku;
    String name;
    Double price;
    Set<ProductCategoryDto> categories;
}
