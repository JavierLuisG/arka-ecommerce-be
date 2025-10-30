package com.store.arka.backend.infrastructure.web.dto.cart.response;

import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToOrderDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponseDto(
    UUID id,
    ProductResponseToOrderDto product,
    Integer quantity,
    LocalDateTime addedAt
) {
}
