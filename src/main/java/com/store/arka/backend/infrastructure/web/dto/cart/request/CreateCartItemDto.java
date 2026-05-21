package com.store.arka.backend.infrastructure.web.dto.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemDto(
    @NotBlank(message = "Product_id is required")
    String productId,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity
) {
}
