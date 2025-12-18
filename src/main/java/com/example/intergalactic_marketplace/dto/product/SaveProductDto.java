package com.example.intergalactic_marketplace.dto.product;

import com.example.intergalactic_marketplace.dto.validation.ExtendedValidation;
import com.example.intergalactic_marketplace.dto.validation.ValidProductGalacticName;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
@GroupSequence({SaveProductDto.class, ExtendedValidation.class})
public class SaveProductDto {
    @NotNull(message = "The name of the product is required")
    @Size(min = 3, max = 255, message = "The name of the product must be between 3 and 255 characters long")
    @ValidProductGalacticName
    String name;

    @NotNull(message = "The SKU of the product is required")
    @Size(min = 3, max = 255, message = "The SKU of the product must be between 3 and 255 characters long")
    String sku;

    String description;

    @NotNull(message = "The product price cannot be null")
    @Min(value = 0, message = "The price of the product must be greater than or equal to zero")
    Double price;

    List<UUID> categoryIds;
}
