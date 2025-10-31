package com.store.arka.backend.infrastructure.web.dto.purchase.request;

import java.util.List;

public record ReceivePurchaseDto(
    List<ReceivePurchaseItemDto> purchaseItems
) {
}
