package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaCartRepository extends JpaRepository<CartEntity, UUID> {
  List<CartEntity> findAllByStatus(CartStatus status);

  List<CartEntity> findAllByCustomerId(UUID customerId);

  List<CartEntity> findAllByItemsProductId(UUID productId);
}
