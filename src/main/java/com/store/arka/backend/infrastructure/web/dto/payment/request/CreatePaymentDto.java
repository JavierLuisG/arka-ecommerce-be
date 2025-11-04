package com.store.arka.backend.infrastructure.web.dto.payment.request;

import com.store.arka.backend.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePaymentDto(
    @NotNull(message = "Order_id is required")
    UUID orderId,
    @NotNull(message = "Payment method is required")
    PaymentMethod method
) {
}
