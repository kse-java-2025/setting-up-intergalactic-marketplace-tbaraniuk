package com.example.intergalactic_marketplace.domain.product;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class ProductCategory {
    UUID uuid;
    String name;
}
