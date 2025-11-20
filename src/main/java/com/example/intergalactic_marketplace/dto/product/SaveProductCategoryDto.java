package com.example.intergalactic_marketplace.dto.product;

import com.example.intergalactic_marketplace.dto.validation.ExtendedValidation;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
@GroupSequence({SaveProductCategoryDto.class, ExtendedValidation.class})
public class SaveProductCategoryDto {
    @NotNull(message = "The name of the product category is required")
    @Size(min = 3, max = 255, message = "The name of the product category must be between 3 and 255 characters long")
    String name;
}
