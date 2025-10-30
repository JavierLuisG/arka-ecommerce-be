package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.infrastructure.web.dto.purchase.request.CreatePurchaseItemDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.response.PurchaseItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseItemDtoMapper {
  private final ProductDtoMapper productDtoMapper;

  public PurchaseItemResponseDto toDto(PurchaseItem domain) {
    return new PurchaseItemResponseDto(
        domain.getId(),
        productDtoMapper.toPurchaseDto(domain.getProduct()),
        domain.getQuantity(),
        domain.getUnitCost(),
        domain.calculateSubtotal(),
        domain.getCreatedAt()
    );
  }

  public List<PurchaseItem> toDomain(List<CreatePurchaseItemDto> listDto) {
    List<PurchaseItem> response = new ArrayList<>();
    listDto.forEach(dto -> {
      response.add(new PurchaseItem(
          null,
          dto.productId(),
          null,
          dto.quantity(),
          null,
          null,
          null
      ));
    });
    return response;
  }

  public List<PurchaseItemResponseDto> toDto(List<PurchaseItem> listDomain) {
    List<PurchaseItemResponseDto> response = new ArrayList<>();
    listDomain.forEach(domain -> {
      response.add(new PurchaseItemResponseDto(
          domain.getId(),
          productDtoMapper.toPurchaseDto(domain.getProduct()),
          domain.getQuantity(),
          domain.getUnitCost(),
          domain.calculateSubtotal(),
          domain.getCreatedAt()
      ));
    });
    return response;
  }
}
