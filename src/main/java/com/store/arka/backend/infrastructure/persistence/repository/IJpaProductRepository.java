package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaProductRepository extends JpaRepository<ProductEntity, UUID> {
  Optional<ProductEntity> findBySku(String sku);

  List<ProductEntity> findAllByStatus(ProductStatus status);

  boolean existsBySku(String sku);
}
