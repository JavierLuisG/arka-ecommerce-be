package com.store.arka.backend.infrastructure.web.dto.product.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductDto(
    @NotBlank(message = "sku is required")
    @Pattern(
        regexp = "^[A-Z]{2}-\\d{3}-\\d{3}$",
        message = "SKU must follow the pattern AA-000-000 (2 letters, hyphen, 3 numbers, hyphen, 3 numbers)"
    )
    String sku,
    @NotBlank(message = "name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÑñ0-9._/\\- ]+$",
        message = "Name can only contain letters (A-Z,a-z,Ñ,ñ), numbers, spaces, dots, slashes, underscores or hyphens;" +
            " no tildes, special symbols, or multiple spaces."
    )
    String name,
    @NotBlank(message = "description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,;:!?()_-]+$",
        message = "Description allows letters (including accents), numbers, single spaces and basic punctuation only"
    )
    String description,
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    BigDecimal price,
    @NotNull(message = "stock is required")
    @Min(value = 1, message = "Stock must be at least 1")
    Integer stock
) {
}
