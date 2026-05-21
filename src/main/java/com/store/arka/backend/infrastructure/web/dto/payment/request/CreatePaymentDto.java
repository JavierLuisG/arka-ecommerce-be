package com.store.arka.backend.infrastructure.web.dto.payment.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentDto(
    @NotBlank(message = "order_id is required")
    String orderId,
    @NotBlank(message = "method is required")
    String method
) {
}
