package com.store.arka.backend.infrastructure.web.dto.product.response;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.infrastructure.web.dto.category.response.CategoryResponseToProductDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponseDto(
    UUID id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    List<CategoryResponseToProductDto> categories,
    Integer stock,
    ProductStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
