package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePurchaseDto(
    @NotBlank(message = "supplier_id is required")
    String supplierId,
    @Valid
    @NotNull(message = "purchase_items is required")
    List<CreatePurchaseItemDto> purchaseItems
) {
}
