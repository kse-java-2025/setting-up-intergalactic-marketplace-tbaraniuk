package com.example.intergalactic_marketplace.dto.product;

import com.example.intergalactic_marketplace.dto.validation.ExtendedValidation;
import com.example.intergalactic_marketplace.dto.validation.ValidProductGalacticName;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
@GroupSequence({SaveProductDto.class, ExtendedValidation.class})
public class SaveProductDto {
    @NotNull(message = "The name of the product is required")
    @ValidProductGalacticName
    String name;

    String description;

    @NotNull(message = "The product price cannot be null")
    @Min(value = 0, message = "The price of the product must be greater than or equal to zero")
    Double price;
}
