package com.example.intergalactic_marketplace.dto.product;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ProductDto {
    UUID uuid;
    String name;
    String description;
    Double price;
}
