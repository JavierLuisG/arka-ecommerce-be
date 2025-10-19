package com.store.arka.backend.infrastructure.web.dto.cart.response;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.infrastructure.web.dto.cartitem.response.CartItemResponseDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseToCartDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CartResponseDto(
    UUID id,
    CustomerResponseToCartDto customer,
    List<CartItemResponseDto> items,
    CartStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime abandonedAt
) {
}
