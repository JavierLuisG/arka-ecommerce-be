package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CartMapper {
  private final CustomerMapper customerMapper;
  private final CartItemMapper cartItemMapper;

  public Cart toDomain(CartEntity entity) {
    if (entity == null) return null;
    return new Cart(
        entity.getId(),
        customerMapper.toDomain(entity.getCustomer()),
        cartItemMapper.toDomain(entity.getItems()),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getAbandonedAt()
    );
  }

  public CartEntity toEntity(Cart domain) {
    if (domain == null) return null;
    CartEntity entity = new CartEntity(
        domain.getId(),
        customerMapper.toReference(domain.getCustomer()),
        new ArrayList<>(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        domain.getAbandonedAt()
    );
    List<CartItemEntity> entityList = cartItemMapper.toEntity(domain.getItems());
    entityList.forEach(item -> item.setCart(entity));
    entity.setItems(entityList);
    return entity;
  }
}
