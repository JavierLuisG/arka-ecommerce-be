package com.store.arka.backend.infrastructure.web.dto.product.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateFieldsProductDto(
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÑñ0-9 _-]+$",
        message = "Name can only contain letters (A-Z,a-z,Ñ,ñ), numbers, single spaces, underscores or hyphens; " +
            "no tildes or special characters allowed"
    )
    String name,
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,;:!?()_-]+$",
        message = "Description allows letters (including accents), numbers, single spaces and basic punctuation only"
    )
    String description,
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    BigDecimal price
) {
}
