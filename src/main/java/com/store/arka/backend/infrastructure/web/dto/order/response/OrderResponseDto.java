package com.store.arka.backend.infrastructure.web.dto.order.response;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseToOrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
    UUID id,
    UUID cartId,
    CustomerResponseToOrderDto customer,
    List<OrderItemResponseDto> items,
    BigDecimal total,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
