package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import org.springframework.stereotype.Component;

@Component
public class CartItemUpdater {
  public CartItemEntity merge(CartItemEntity entity, CartItem domain) {
    if (entity == null || domain == null) return null;
    if (!entity.getQuantity().equals(domain.getQuantity()))
      entity.setQuantity(domain.getQuantity());
    return entity;
  }
}
