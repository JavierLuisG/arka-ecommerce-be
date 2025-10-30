package com.store.arka.backend.infrastructure.web.dto.purchase.response;

import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToPurchaseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PurchaseItemResponseDto(
    UUID id,
    ProductResponseToPurchaseDto product,
    Integer quantity,
    BigDecimal unitCost,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {
}
