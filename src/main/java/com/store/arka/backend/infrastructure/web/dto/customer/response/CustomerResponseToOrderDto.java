package com.store.arka.backend.infrastructure.web.dto.customer.response;

import com.store.arka.backend.domain.enums.Country;

import java.util.UUID;

public record CustomerResponseToOrderDto(
    UUID id,
    UUID userId,
    DocumentResponseToCustomerDto document,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    String city,
    Country country
) {
}
