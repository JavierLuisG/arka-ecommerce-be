package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaPurchaseRepository extends JpaRepository<PurchaseEntity, UUID> {
  List<PurchaseEntity> findAllByStatus(PurchaseStatus status);

  List<PurchaseEntity> findAllBySupplierId(UUID supplierId);

  List<PurchaseEntity> findAllByItemsProductId(UUID productId);

  List<PurchaseEntity> findAllByItemsProductIdAndStatus(UUID productId, PurchaseStatus status);
}
