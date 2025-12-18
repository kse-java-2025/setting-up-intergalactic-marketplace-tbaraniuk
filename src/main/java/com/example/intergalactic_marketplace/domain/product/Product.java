package com.example.intergalactic_marketplace.domain.product;

import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Product {
    UUID uuid;
    String sku;
    String name;
    String description;
    Double price;

    @Builder.Default
    Set<UUID> categories = Set.of();
}
