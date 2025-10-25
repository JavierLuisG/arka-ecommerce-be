package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaOrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
  List<OrderItemEntity> findAllByProductId(UUID productId);
}
