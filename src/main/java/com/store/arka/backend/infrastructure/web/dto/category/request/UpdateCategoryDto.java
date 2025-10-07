package com.store.arka.backend.infrastructure.web.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDto(
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,;:!?()_-]+$",
        message = "Description allows letters (including accents), numbers, single spaces and basic punctuation only"
    )
    String description
) {
}
