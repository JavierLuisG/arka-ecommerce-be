package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Purchase;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseMapper {
  private final SupplierMapper supplierMapper;
  private final PurchaseItemMapper purchaseItemMapper;

  public Purchase toDomain(PurchaseEntity entity) {
    if (entity == null) return null;
    return new Purchase(
        entity.getId(),
        supplierMapper.toDomain(entity.getSupplier()),
        purchaseItemMapper.toDomain(entity.getItems()),
        entity.getTotal(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public PurchaseEntity toEntity(Purchase domain) {
    if (domain == null) return null;
    PurchaseEntity entity = new PurchaseEntity(
        domain.getId(),
        supplierMapper.toReference(domain.getSupplier()),
        new ArrayList<>(),
        domain.getTotal(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
    List<PurchaseItemEntity> entityList = purchaseItemMapper.toEntity(domain.getItems());
    entityList.forEach(item -> item.setPurchase(entity));
    entity.setItems(entityList);
    return entity;
  }
}
