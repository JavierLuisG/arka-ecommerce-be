package com.store.arka.backend.infrastructure.web.dto.category.response;

import java.util.UUID;

public record CategoryResponseToProductDto(
    UUID id,
    String name
) {
}
