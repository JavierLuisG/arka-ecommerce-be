package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.OrderItem;
import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemUpdater {
  public OrderItemEntity merge(OrderItemEntity entity, OrderItem domain) {
    if (entity == null || domain == null) return null;
    if (domain.getQuantity() != null && !entity.getQuantity().equals(domain.getQuantity()))
      entity.setQuantity(domain.getQuantity());
    if (domain.getProductPrice() != null && !entity.getProductPrice().equals(domain.getProductPrice()))
      entity.setProductPrice(domain.getProductPrice());
    if (domain.getSubtotal() != null && !entity.getSubtotal().equals(domain.getSubtotal()))
      entity.setSubtotal(domain.getSubtotal());
    return entity;
  }
}
