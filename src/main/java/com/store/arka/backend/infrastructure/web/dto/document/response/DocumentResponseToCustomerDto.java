package com.store.arka.backend.infrastructure.web.dto.document.response;

import com.store.arka.backend.domain.enums.DocumentType;

import java.util.UUID;

public record DocumentResponseToCustomerDto(
    UUID id,
    DocumentType type,
    String number
) {
}
