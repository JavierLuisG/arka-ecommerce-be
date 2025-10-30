package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IOrderItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.OrderItem;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.OrderItemMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaOrderItemRepository;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaOrderRepository;
import com.store.arka.backend.infrastructure.persistence.updater.OrderItemUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderItemPersistenceAdapter implements IOrderItemAdapterPort {
  private final IJpaOrderItemRepository jpaOrderItemRepository;
  private final IJpaOrderRepository orderRepository;
  private final OrderItemMapper mapper;
  private final OrderItemUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public OrderItem saveAddOrderItem(UUID orderId, OrderItem orderItem) {
    OrderEntity orderEntity = orderRepository.findById(orderId)
        .orElseThrow(() -> new ModelNotFoundException("Order with id " + orderId + " not found"));
    OrderItemEntity orderItemEntity = mapper.toEntityWithOrder(orderEntity, orderItem);
    orderItemEntity.setProduct(
        entityManager.getReference(ProductEntity.class, orderItemEntity.getProduct().getId()));
    OrderItemEntity saved = jpaOrderItemRepository.save(orderItemEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public OrderItem saveUpdateOrderItem(OrderItem orderItem) {
    OrderItemEntity orderItemEntity = jpaOrderItemRepository.findById(orderItem.getId())
        .orElseThrow(() -> new ModelNotFoundException("OrderItem with id " + orderItem.getId() + " not found"));
    OrderItemEntity updated = updater.merge(orderItemEntity, orderItem);
    OrderItemEntity saved = jpaOrderItemRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<OrderItem> findOrderItemById(UUID id) {
    return jpaOrderItemRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<OrderItem> findAllOrderItems() {
    return jpaOrderItemRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<OrderItem> findAllOrderItemsByProductId(UUID productId) {
    return jpaOrderItemRepository.findAllByProductId(productId).stream().map(mapper::toDomain).collect(Collectors.toList());
  }
}
