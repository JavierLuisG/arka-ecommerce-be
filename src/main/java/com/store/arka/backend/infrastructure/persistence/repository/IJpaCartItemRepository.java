package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaCartItemRepository extends JpaRepository<CartItemEntity, UUID> {
  List<CartItemEntity> findAllByProductId(UUID productId);
}
