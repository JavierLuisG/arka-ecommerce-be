package com.store.arka.backend.infrastructure.web.dto.category.response;

import com.store.arka.backend.domain.enums.CategoryStatus;

import java.time.LocalDateTime;

import java.util.UUID;

public record CategoryResponseDto(
    UUID id,
    String name,
    String description,
    CategoryStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
