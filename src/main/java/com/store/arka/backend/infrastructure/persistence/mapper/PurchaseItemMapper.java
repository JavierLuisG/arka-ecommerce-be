package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseItemMapper {
  private final ProductMapper productMapper;

  public PurchaseItem toDomain(PurchaseItemEntity entity) {
    if (entity == null) return null;
    return new PurchaseItem(
        entity.getId(),
        entity.getProduct().getId(),
        productMapper.toDomain(entity.getProduct()),
        entity.getQuantity(),
        entity.getUnitCost(),
        entity.getSubtotal(),
        entity.getCreatedAt()
    );
  }

  public PurchaseItemEntity toEntityWithPurchase(PurchaseEntity entity, PurchaseItem domain) {
    if (entity == null || domain == null) return null;
    return new PurchaseItemEntity(
        domain.getId(),
        productMapper.toReference(domain.getProductId()),
        domain.getQuantity(),
        domain.getUnitCost(),
        domain.getSubtotal(),
        entity,
        domain.getCreatedAt()
    );
  }

  public List<PurchaseItem> toDomain(List<PurchaseItemEntity> listEntity) {
    if (listEntity == null) return Collections.emptyList();
    List<PurchaseItem> itemList = new ArrayList<>();
    listEntity.forEach(entity -> {
      itemList.add(new PurchaseItem(
          entity.getId(),
          entity.getProduct().getId(),
          productMapper.toDomain(entity.getProduct()),
          entity.getQuantity(),
          entity.getUnitCost(),
          entity.getSubtotal(),
          entity.getCreatedAt()
      ));
    });
    return itemList;
  }

  public List<PurchaseItemEntity> toEntity(List<PurchaseItem> listDomain) {
    if (listDomain == null) return Collections.emptyList();
    List<PurchaseItemEntity> entityList = new ArrayList<>();
    listDomain.forEach(domain -> {
      entityList.add(new PurchaseItemEntity(
          domain.getId(),
          productMapper.toReference(domain.getProduct().getId()),
          domain.getQuantity(),
          domain.getUnitCost(),
          domain.getSubtotal(),
          null,
          domain.getCreatedAt()
      ));
    });
    return entityList;
  }
}
