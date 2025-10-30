package com.store.arka.backend.infrastructure.web.dto.cart.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateCartDto(
    @NotNull(message = "Customer_id is required")
    UUID customerId,
    @Valid
    @NotNull(message = "Cart_items is required")
    List<CreateCartItemDto> cartItems
) {
}
