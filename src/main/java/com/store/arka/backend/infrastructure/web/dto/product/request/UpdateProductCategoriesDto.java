package com.store.arka.backend.infrastructure.web.dto.product.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record UpdateProductCategoriesDto(
    @NotNull(message = "categories is required")
    Set<UUID> categories
) {
}
