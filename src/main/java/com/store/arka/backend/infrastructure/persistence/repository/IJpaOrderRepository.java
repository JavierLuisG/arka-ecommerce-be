package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
  Optional<OrderEntity> findByIdAndStatus(UUID id, OrderStatus status);

  Optional<OrderEntity> findByIdAndCustomerId(UUID id, UUID customerId);

  Optional<OrderEntity> findByIdAndCustomerIdAndStatus(UUID id, UUID customerId, OrderStatus status);

  List<OrderEntity> findAllByStatus(OrderStatus status);

  List<OrderEntity> findAllByCustomerId(UUID customerId);

  List<OrderEntity> findAllByCustomerIdAndStatus(UUID customerId, OrderStatus status);

  List<OrderEntity> findAllByItemsProductId(UUID productId);

  List<OrderEntity> findAllByItemsProductIdAndStatus(UUID productId, OrderStatus status);

  List<OrderEntity> findAllByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, OrderStatus status);

  boolean existsByCartId(UUID cartId);
}
