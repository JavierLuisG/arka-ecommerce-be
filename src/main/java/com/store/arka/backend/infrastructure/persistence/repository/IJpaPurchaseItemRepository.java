package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IJpaPurchaseItemRepository extends JpaRepository<PurchaseItemEntity, UUID> {
  List<PurchaseItemEntity> findAllByProductId(UUID productId);
}
