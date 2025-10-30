package com.store.arka.backend.infrastructure.web.dto.customer.response;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponseDto(
    UUID id,
    DocumentType type,
    String number,
    DocumentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
