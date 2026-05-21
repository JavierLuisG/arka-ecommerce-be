package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePurchaseItemDto(
    @NotBlank(message = "product_id is required")
    String productId,
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    Integer quantity,
    @NotNull(message = "unit_cost is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Purchase price must be greater than 0")
    BigDecimal unitCost
) {
}
