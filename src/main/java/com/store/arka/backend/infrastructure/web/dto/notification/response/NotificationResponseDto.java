package com.store.arka.backend.infrastructure.web.dto.notification.response;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseToOrderDto;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDto(
    UUID id,
    CustomerResponseToOrderDto customer,
    OrderResponseDto order,
    NotificationType type,
    String message,
    NotificationStatus status,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {
}
