package com.store.arka.backend.infrastructure.web.dto.notification.response;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDto(
    UUID id,
    UUID customerId,
    UUID orderId,
    NotificationType type,
    String message,
    NotificationStatus status,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {
}
