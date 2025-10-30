package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaPurchaseRepository extends JpaRepository<PurchaseEntity, UUID> {
  Optional<PurchaseEntity> findByIdAndStatus(UUID id, PurchaseStatus status);

  Optional<PurchaseEntity> findByIdAndSupplierId(UUID id, UUID supplierId);

  Optional<PurchaseEntity> findByIdAndSupplierIdAndStatus(UUID id, UUID customerId, PurchaseStatus status);

  List<PurchaseEntity> findAllByStatus(PurchaseStatus status);

  List<PurchaseEntity> findAllBySupplierId(UUID supplierId);

  List<PurchaseEntity> findAllBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status);

  List<PurchaseEntity> findAllByItemsProductId(UUID productId);

  List<PurchaseEntity> findAllByItemsProductIdAndStatus(UUID productId, PurchaseStatus status);

  List<PurchaseEntity> findAllBySupplierIdAndItemsProductIdAndStatus(UUID supplierId, UUID productId, PurchaseStatus status);
}
