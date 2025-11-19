package com.store.arka.backend.infrastructure.web.dto.payment.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentMethodDto(
    @NotBlank(message = "method is required")
    String method
) {
}
