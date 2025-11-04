package com.store.arka.backend.infrastructure.web.dto.payment.response;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(
    UUID id,
    UUID order,
    BigDecimal amount,
    PaymentMethod method,
    PaymentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime processedAt
) {
}
