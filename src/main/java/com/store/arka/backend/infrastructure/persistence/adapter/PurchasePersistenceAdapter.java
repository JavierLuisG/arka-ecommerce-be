package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IPurchaseAdapterPort;
import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Purchase;
import com.store.arka.backend.infrastructure.persistence.entity.*;
import com.store.arka.backend.infrastructure.persistence.mapper.PurchaseMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaPurchaseRepository;
import com.store.arka.backend.infrastructure.persistence.updater.PurchaseUpdater;
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
public class PurchasePersistenceAdapter implements IPurchaseAdapterPort {
  private final IJpaPurchaseRepository jpaPurchaseRepository;
  private final PurchaseMapper mapper;
  private final PurchaseUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Purchase saveCreatePurchase(Purchase purchase) {
    PurchaseEntity purchaseEntity = mapper.toEntity(purchase);
    if (purchaseEntity.getSupplier() != null && purchaseEntity.getSupplier().getId() != null) {
      purchaseEntity.setSupplier(
          entityManager.getReference(SupplierEntity.class, purchaseEntity.getSupplier().getId()));
    }
    if (purchaseEntity.getItems() != null) {
      purchaseEntity.getItems().forEach(item -> {
        if (item.getProduct() != null && item.getProduct().getId() != null) {
          item.setProduct(entityManager.getReference(ProductEntity.class, item.getProduct().getId()));
        }
        item.setPurchase(purchaseEntity);
      });
    }
    PurchaseEntity saved = jpaPurchaseRepository.save(purchaseEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Purchase saveUpdatePurchase(Purchase purchase) {
    PurchaseEntity entity = jpaPurchaseRepository.findById(purchase.getId())
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + purchase.getId() + " not found"));
    PurchaseEntity updated = updater.merge(entity, purchase);
    PurchaseEntity saved = jpaPurchaseRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Purchase> findPurchaseById(UUID id) {
    return jpaPurchaseRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Purchase> findPurchaseByIdAndStatus(UUID id, PurchaseStatus status) {
    return jpaPurchaseRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Purchase> findPurchaseByIdAndSupplierId(UUID id, UUID supplierId) {
    return jpaPurchaseRepository.findByIdAndSupplierId(id, supplierId).map(mapper::toDomain);
  }

  @Override
  public Optional<Purchase> findPurchaseByIdAndSupplierIdAndStatus(UUID id, UUID supplierId, PurchaseStatus status) {
    return jpaPurchaseRepository.findByIdAndSupplierIdAndStatus(id, supplierId, status).map(mapper::toDomain);
  }

  @Override
  public List<Purchase> findAllPurchases() {
    return jpaPurchaseRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesByStatus(PurchaseStatus status) {
    return jpaPurchaseRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesBySupplierId(UUID supplierId) {
    return jpaPurchaseRepository.findAllBySupplierId(supplierId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status) {
    return jpaPurchaseRepository.findAllBySupplierIdAndStatus(supplierId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesByItemsProductId(UUID productId) {
    return jpaPurchaseRepository.findAllByItemsProductId(productId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status) {
    return jpaPurchaseRepository.findAllByItemsProductIdAndStatus(productId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Purchase> findAllPurchasesBySupplierIdAndItemsProductIdAndStatus(
      UUID supplierId, UUID productId, PurchaseStatus status) {
    return jpaPurchaseRepository.findAllBySupplierIdAndItemsProductIdAndStatus(supplierId, productId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public void deletePurchaseById(UUID id) {
    jpaPurchaseRepository.deleteById(id);
  }
}
