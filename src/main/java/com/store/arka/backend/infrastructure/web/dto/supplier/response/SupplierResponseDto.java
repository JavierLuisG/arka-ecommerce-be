package com.store.arka.backend.infrastructure.web.dto.supplier.response;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierResponseDto(
    UUID id,
    String commercialName,
    String contactName,
    String email,
    String phone,
    String taxId,
    String address,
    String city,
    Country country,
    SupplierStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
