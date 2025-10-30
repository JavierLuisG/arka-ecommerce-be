package com.store.arka.backend.infrastructure.web.dto.product.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseToPurchaseDto(
    UUID id,
    String sku,
    String name,
    String description,
    BigDecimal price
) {
}
