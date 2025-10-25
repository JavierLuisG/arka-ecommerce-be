package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.OrderItem;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {
  private final ProductMapper productMapper;

  public OrderItem toDomain(OrderItemEntity entity) {
    if (entity == null) return null;
    return new OrderItem(
        entity.getId(),
        entity.getProduct().getId(),
        productMapper.toDomain(entity.getProduct()),
        entity.getQuantity(),
        entity.getProductPrice(),
        entity.getSubtotal(),
        entity.getCreatedAt()
    );
  }

  public OrderItemEntity toEntityWithOrder(OrderEntity entity, OrderItem domain) {
    if (entity == null || domain == null) return null;
    return new OrderItemEntity(
        domain.getId(),
        productMapper.toReference(domain.getProductId()),
        domain.getQuantity(),
        domain.getProductPrice(),
        domain.getSubtotal(),
        entity,
        domain.getCreatedAt()
    );
  }

  public List<OrderItem> toDomain(List<OrderItemEntity> listEntity) {
    if (listEntity == null) return Collections.emptyList();
    List<OrderItem> itemList = new ArrayList<>();
    listEntity.forEach(entity -> {
      itemList.add(new OrderItem(
          entity.getId(),
          entity.getProduct().getId(),
          productMapper.toDomain(entity.getProduct()),
          entity.getQuantity(),
          entity.getProductPrice(),
          entity.getSubtotal(),
          entity.getCreatedAt()
      ));
    });
    return itemList;
  }

  public List<OrderItemEntity> toEntity(List<OrderItem> listDomain) {
    if (listDomain == null) return Collections.emptyList();
    List<OrderItemEntity> entityList = new ArrayList<>();
    listDomain.forEach(domain -> {
      entityList.add(new OrderItemEntity(
          domain.getId(),
          productMapper.toReference(domain.getProduct().getId()),
          domain.getQuantity(),
          domain.getProductPrice(),
          domain.getSubtotal(),
          null,
          domain.getCreatedAt()
      ));
    });
    return entityList;
  }
}
