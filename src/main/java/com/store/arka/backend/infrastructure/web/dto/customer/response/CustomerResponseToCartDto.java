package com.store.arka.backend.infrastructure.web.dto.customer.response;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.infrastructure.web.dto.document.response.DocumentResponseToCustomerDto;

import java.util.UUID;

public record CustomerResponseToCartDto(
    UUID id,
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
