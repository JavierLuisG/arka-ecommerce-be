package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.domain.model.Purchase;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.PurchaseItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseUpdater {
  private final PurchaseItemMapper purchaseItemMapper;

  public PurchaseEntity merge(PurchaseEntity entity, Purchase domain) {
    if (entity == null || domain == null) return null;
    if (domain.getTotal() != null && !entity.getTotal().equals(domain.getTotal()))
      entity.setTotal(domain.getTotal());
    if (domain.getStatus() != null && !entity.getStatus().equals(domain.getStatus()))
      entity.setStatus(domain.getStatus());
    // remove excess items to return
    List<PurchaseItemEntity> entityList = purchaseItemMapper.toEntity(domain.getItems());
    entity.getItems().removeIf(existingItem ->
        entityList.stream().noneMatch(newItem ->
            newItem.getId() != null && newItem.getId().equals(existingItem.getId()))
    );
    return entity;
  }
}
