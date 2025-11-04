package com.store.arka.backend.infrastructure.web.dto.payment.request;

import com.store.arka.backend.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentMethodDto(
    @NotNull(message = "Payment method is required")
    PaymentMethod method
) {
}
