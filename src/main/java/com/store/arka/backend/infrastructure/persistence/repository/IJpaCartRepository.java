package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaCartRepository extends JpaRepository<CartEntity, UUID> {
  Optional<CartEntity> findByIdAndStatus(UUID id, CartStatus status);

  Optional<CartEntity> findByIdAndCustomerId(UUID id, UUID customerId);

  Optional<CartEntity> findByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status);

  List<CartEntity> findAllByStatus(CartStatus status);

  List<CartEntity> findAllByCustomerId(UUID customerId);

  List<CartEntity> findAllByCustomerIdAndStatus(UUID customerId, CartStatus status);

  List<CartEntity> findAllByItemsProductId(UUID productId);

  List<CartEntity> findAllByItemsProductIdAndStatus(UUID productId, CartStatus status);

  List<CartEntity> findAllByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, CartStatus status);
}
