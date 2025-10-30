package com.store.arka.backend.infrastructure.web.dto.customer.response;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponseDto(
    UUID id,
    DocumentResponseToCustomerDto document,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    String city,
    Country country,
    CustomerStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
