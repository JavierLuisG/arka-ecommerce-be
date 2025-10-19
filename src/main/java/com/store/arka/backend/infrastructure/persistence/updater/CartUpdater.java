package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.CartItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartUpdater {
  private final CartItemMapper cartItemMapper;

  public CartEntity merge(CartEntity entity, Cart domain) {
    if (entity == null || domain == null) return null;
    if (!entity.getStatus().equals(domain.getStatus())) {
      entity.setStatus(domain.getStatus());
    }
    // remove excess items to return -> the allItems
    List<CartItemEntity> domainItems = cartItemMapper.toEntity(domain.getItems());
    entity.getItems().removeIf(existingItem ->
        domainItems.stream().noneMatch(newItem ->
            newItem.getId() != null && newItem.getId().equals(existingItem.getId()))
    );
    return entity;
  }
}
