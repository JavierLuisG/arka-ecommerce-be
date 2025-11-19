package com.store.arka.backend.infrastructure.web.dto.customer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentRequestDto(
    @NotBlank(message = "type is required")
    String type,
    @NotBlank(message = "number is required")
    @Size(min = 10, max = 15, message = "Document number must be between 10 and 15 characters")
    String number
) {
}
