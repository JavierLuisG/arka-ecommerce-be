package com.store.arka.backend.infrastructure.web.dto.product.response;

public record CheckProductResponseDto(
    boolean available,
    int requestedQuantity,
    int currentStock
) {
}
