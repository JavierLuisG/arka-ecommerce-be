package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import org.springframework.stereotype.Component;

@Component
public class PurchaseItemUpdater {
  public PurchaseItemEntity merge(PurchaseItemEntity entity, PurchaseItem domain) {
    if (entity == null || domain == null) return null;
    if (domain.getQuantity() != null && !entity.getQuantity().equals(domain.getQuantity()))
      entity.setQuantity(domain.getQuantity());
    if (domain.getUnitCost() != null && !entity.getUnitCost().equals(domain.getUnitCost()))
      entity.setUnitCost(domain.getUnitCost());
    if (domain.getSubtotal() != null && !entity.getSubtotal().equals(domain.getSubtotal()))
      entity.setSubtotal(domain.getSubtotal());
    return entity;
  }
}
