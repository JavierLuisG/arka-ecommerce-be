package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaOrderRepository extends JpaRepository<OrderEntity, UUID> {

  List<OrderEntity> findAllByStatus(OrderStatus status);

  List<OrderEntity> findAllByCustomerId(UUID customerId);

  List<OrderEntity> findAllByItemsProductId(UUID productId);

  boolean existsByCartId(UUID cartId);
}
