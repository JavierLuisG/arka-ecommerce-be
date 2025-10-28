package com.store.arka.backend.infrastructure.web.dto.product.response;

import java.util.UUID;

public record ProductResponseToSupplierDto(
    UUID id,
    String sku,
    String name
) {
}
