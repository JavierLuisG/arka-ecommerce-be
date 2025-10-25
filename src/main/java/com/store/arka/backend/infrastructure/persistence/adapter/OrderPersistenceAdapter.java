package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.OrderMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaOrderRepository;
import com.store.arka.backend.infrastructure.persistence.updater.OrderUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements IOrderAdapterPort {
  private final IJpaOrderRepository jpaOrderRepository;
  private final OrderMapper mapper;
  private final OrderUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Order saveCreateOrder(Order order) {
    OrderEntity orderEntity = mapper.toEntity(order);
    if (orderEntity.getCustomer() != null && orderEntity.getCustomer().getId() != null) {
      orderEntity.setCustomer(
          entityManager.getReference(orderEntity.getCustomer().getClass(), orderEntity.getCustomer().getId()));
    }
    if (orderEntity.getItems() != null) {
      orderEntity.getItems().forEach(item -> {
        if (item.getProduct() != null && item.getProduct().getId() != null) {
          item.setProduct(entityManager.getReference(item.getProduct().getClass(), item.getProduct().getId()));
        }
        item.setOrder(orderEntity);
      });
    }
    OrderEntity saved = jpaOrderRepository.save(orderEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Order saveUpdateOrder(Order order) {
    OrderEntity entity = jpaOrderRepository.findById(order.getId())
        .orElseThrow(() -> new ModelNotFoundException("Order with id " + order.getId() + " not found"));
    OrderEntity updated = updater.merge(entity, order);
    OrderEntity saved = jpaOrderRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Order> findOrderById(UUID id) {
    return jpaOrderRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Order> findOrderByIdAndStatus(UUID id, OrderStatus status) {
    return jpaOrderRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Order> findOrderByIdAndCustomerId(UUID id, UUID customerId) {
    return jpaOrderRepository.findByIdAndCustomerId(id, customerId).map(mapper::toDomain);
  }

  @Override
  public Optional<Order> findOrderByIdAndCustomerIdAndStatus(UUID id, UUID customerId, OrderStatus status) {
    return jpaOrderRepository.findByIdAndCustomerIdAndStatus(id, customerId, status).map(mapper::toDomain);
  }

  @Override
  public List<Order> findAllOrders() {
    return jpaOrderRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByStatus(OrderStatus status) {
    return jpaOrderRepository.findAllByStatus(status).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByCustomerId(UUID customerId) {
    return jpaOrderRepository.findAllByCustomerId(customerId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
    return jpaOrderRepository.findAllByCustomerIdAndStatus(customerId, status).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByItemsProductId(UUID productId) {
    return jpaOrderRepository.findAllByItemsProductId(productId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByItemsProductIdAndStatus(UUID productId, OrderStatus status) {
    return jpaOrderRepository.findAllByItemsProductIdAndStatus(productId, status)
        .stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Order> findAllOrdersByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, OrderStatus status) {
    return jpaOrderRepository.findAllByCustomerIdAndItemsProductIdAndStatus(customerId, productId, status)
        .stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsByCartId(UUID cartId) {
    return jpaOrderRepository.existsByCartId(cartId);
  }
}
