package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Purchase;
import com.store.arka.backend.infrastructure.web.dto.purchase.request.CreatePurchaseDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.response.PurchaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseDtoMapper {
  private final SupplierDtoMapper supplierDtoMapper;
  private final PurchaseItemDtoMapper purchaseItemDtoMapper;

  public Purchase toDomain(CreatePurchaseDto dto) {
    return new Purchase(
        null,
        null,
        purchaseItemDtoMapper.toDomain(dto.purchaseItems()),
        null,
        null,
        null,
        null
    );
  }

  public PurchaseResponseDto toDto(Purchase domain) {
    return new PurchaseResponseDto(
        domain.getId(),
        supplierDtoMapper.toPurchaseDto(domain.getSupplier()),
        purchaseItemDtoMapper.toDto(domain.getItems()),
        domain.getTotal(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }
}
