package com.store.arka.backend.infrastructure.web.dto.order.response;

import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToOrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderItemResponseDto(
    UUID id,
    ProductResponseToOrderDto product,
    Integer quantity,
    BigDecimal productPrice,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {
}
