package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.infrastructure.persistence.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaSupplierRepository extends JpaRepository<SupplierEntity, UUID> {
  Optional<SupplierEntity> findByIdAndStatus(UUID id, SupplierStatus status);

  Optional<SupplierEntity> findByEmail(String email);

  Optional<SupplierEntity> findByEmailAndStatus(String email, SupplierStatus status);

  Optional<SupplierEntity> findByTaxId(String taxId);

  Optional<SupplierEntity> findByTaxIdAndStatus(String taxId, SupplierStatus status);

  List<SupplierEntity> findAllByStatus(SupplierStatus status);

  boolean existsByEmail(String email);

  boolean existsByTaxId(String taxId);
}
