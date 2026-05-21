package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReceivePurchaseItemDto(
    @NotBlank(message = "product_id is required")
    String productId,
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity
) {
}
