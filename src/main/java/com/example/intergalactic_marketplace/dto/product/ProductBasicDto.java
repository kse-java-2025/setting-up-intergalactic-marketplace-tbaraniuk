package com.example.intergalactic_marketplace.dto.product;

import lombok.*;
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
    String name;
    String description;
    Double price;
    Set<ProductCategoryDto> categories;
}
