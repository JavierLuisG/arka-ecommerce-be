package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseItemDto(
    @NotNull(message = "Product_id is required")
    UUID productId,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity
) {
}
