package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePurchaseDto(
    @NotNull(message = "Supplier_id is required")
    UUID supplierId,
    @Valid
    @NotNull(message = "Purchase_items is required")
    List<ReceivePurchaseItemDto> purchaseItems
) {
}
