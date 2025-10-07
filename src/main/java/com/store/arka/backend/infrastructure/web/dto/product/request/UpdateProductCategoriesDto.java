package com.store.arka.backend.infrastructure.web.dto.product.request;

import java.util.Set;
import java.util.UUID;

public record UpdateProductCategoriesDto(
    Set<UUID> categories
) {
}
