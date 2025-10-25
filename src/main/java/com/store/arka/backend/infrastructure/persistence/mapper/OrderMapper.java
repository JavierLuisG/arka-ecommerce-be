package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {
  private final CustomerMapper customerMapper;
  private final OrderItemMapper orderItemMapper;

  public Order toDomain(OrderEntity entity) {
    return new Order(
        entity.getId(),
        entity.getCartId(),
        customerMapper.toDomain(entity.getCustomer()),
        orderItemMapper.toDomain(entity.getItems()),
        entity.getTotal(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public OrderEntity toEntity(Order domain) {
    OrderEntity entity = new OrderEntity(
        domain.getId(),
        domain.getCartId(),
        customerMapper.toReference(domain.getCustomer()),
        new ArrayList<>(),
        domain.getTotal(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
    List<OrderItemEntity> entityList = orderItemMapper.toEntity(domain.getItems());
    entityList.forEach(item -> item.setOrder(entity));
    entity.setItems(entityList);
    return entity;
  }
}
