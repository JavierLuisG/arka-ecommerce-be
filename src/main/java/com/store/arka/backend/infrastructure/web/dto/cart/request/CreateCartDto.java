package com.store.arka.backend.infrastructure.web.dto.cart.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateCartDto(
    @NotBlank(message = "Customer_id is required")
    String customerId,
    @Valid
    @NotNull(message = "Cart_items is required")
    List<CreateCartItemDto> cartItems
) {
}
