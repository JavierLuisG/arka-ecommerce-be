package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IPurchaseItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseEntity;
import com.store.arka.backend.infrastructure.persistence.entity.PurchaseItemEntity;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.PurchaseItemMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaPurchaseItemRepository;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaPurchaseRepository;
import com.store.arka.backend.infrastructure.persistence.updater.PurchaseItemUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PurchaseItemPersistenceAdapter implements IPurchaseItemAdapterPort {
  private final IJpaPurchaseItemRepository jpaPurchaseItemRepository;
  private final IJpaPurchaseRepository purchaseRepository;
  private final PurchaseItemMapper mapper;
  private final PurchaseItemUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public PurchaseItem saveAddPurchaseItem(UUID purchaseId, PurchaseItem purchaseItem) {
    PurchaseEntity purchaseEntity = purchaseRepository.findById(purchaseId)
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + purchaseId + " not found"));
    PurchaseItemEntity purchaseItemEntity = mapper.toEntityWithPurchase(purchaseEntity, purchaseItem);
    purchaseItemEntity.setProduct(
        entityManager.getReference(ProductEntity.class, purchaseItemEntity.getProduct().getId()));
    PurchaseItemEntity saved = jpaPurchaseItemRepository.save(purchaseItemEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public PurchaseItem saveUpdatePurchaseItem(PurchaseItem purchaseItem) {
    PurchaseItemEntity purchaseItemEntity = jpaPurchaseItemRepository.findById(purchaseItem.getId())
        .orElseThrow(() -> new ModelNotFoundException("PurchaseItem with id " + purchaseItem.getId() + " not found"));
    PurchaseItemEntity updated = updater.merge(purchaseItemEntity, purchaseItem);
    PurchaseItemEntity saved = jpaPurchaseItemRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<PurchaseItem> findPurchaseItemById(UUID id) {
    return jpaPurchaseItemRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<PurchaseItem> findAllPurchaseItems() {
    return jpaPurchaseItemRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<PurchaseItem> findAllPurchaseItemsByProductId(UUID productId) {
    return jpaPurchaseItemRepository.findAllByProductId(productId).stream().map(mapper::toDomain).collect(Collectors.toList());
  }
}
