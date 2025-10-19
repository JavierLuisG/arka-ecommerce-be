package com.store.arka.backend.infrastructure.web.dto.cartitem.response;

import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToCartDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponseDto(
    UUID id,
    ProductResponseToCartDto product,
    Integer quantity,
    LocalDateTime addedAt
) {
}
