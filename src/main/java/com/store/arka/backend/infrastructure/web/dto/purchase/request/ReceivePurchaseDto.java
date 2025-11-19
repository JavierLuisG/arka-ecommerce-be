package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReceivePurchaseDto(
    @Valid
    @NotNull(message = "purchase_items is required")
    List<ReceivePurchaseItemDto> purchaseItems
) {
}
