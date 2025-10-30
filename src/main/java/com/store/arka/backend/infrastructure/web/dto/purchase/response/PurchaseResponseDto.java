package com.store.arka.backend.infrastructure.web.dto.purchase.response;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.infrastructure.web.dto.supplier.response.SupplierResponseToPurchaseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseResponseDto(
    UUID id,
    SupplierResponseToPurchaseDto supplier,
    List<PurchaseItemResponseDto> items,
    BigDecimal total,
    PurchaseStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
