package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ReceivePurchaseItemDto(
    @NotNull(message = "Product_id is required")
    UUID productId,
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity,
    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Purchase price must be greater than 0")
    BigDecimal unitCost
) {
}
