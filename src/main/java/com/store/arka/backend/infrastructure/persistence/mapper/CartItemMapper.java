package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CartItemMapper {
  private final ProductMapper productMapper;

  public CartItem toDomain(CartItemEntity entity) {
    if (entity == null) return null;
    return new CartItem(
        entity.getId(),
        entity.getProduct().getId(),
        productMapper.toDomain(entity.getProduct()),
        entity.getQuantity(),
        entity.getAddedAt()
    );
  }

  public CartItemEntity toEntityWithCart(CartEntity entity, CartItem domain) {
    if (entity == null || domain == null) return null;
    return new CartItemEntity(
        domain.getId(),
        productMapper.toReference(domain.getProductId()),
        domain.getQuantity(),
        entity,
        domain.getAddedAt()
    );
  }

  public List<CartItem> toDomain(List<CartItemEntity> listEntity) {
    if (listEntity == null) return Collections.emptyList();
    List<CartItem> listCartItems = new ArrayList<>();
    listEntity.forEach(entity -> {
      listCartItems.add(new CartItem(
          entity.getId(),
          entity.getProduct().getId(),
          productMapper.toDomain(entity.getProduct()),
          entity.getQuantity(),
          entity.getAddedAt()
      ));
    });
    return listCartItems;
  }

  public List<CartItemEntity> toEntity(List<CartItem> listDomain) {
    if (listDomain == null) return Collections.emptyList();
    List<CartItemEntity> listCartItemEntities = new ArrayList<>();
    listDomain.forEach(domain -> {
      listCartItemEntities.add(new CartItemEntity(
          domain.getId(),
          productMapper.toReference(domain.getProductId()),
          domain.getQuantity(),
          null,
          domain.getAddedAt()
      ));
    });
    return listCartItemEntities;
  }
}
