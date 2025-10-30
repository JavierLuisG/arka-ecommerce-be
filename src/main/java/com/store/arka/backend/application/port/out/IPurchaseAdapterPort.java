package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.model.Purchase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPurchaseAdapterPort {
  Purchase saveCreatePurchase(Purchase purchase);

  Purchase saveUpdatePurchase(Purchase purchase);

  Optional<Purchase> findPurchaseById(UUID id);

  Optional<Purchase> findPurchaseByIdAndStatus(UUID id, PurchaseStatus status);

  Optional<Purchase> findPurchaseByIdAndSupplierId(UUID id, UUID supplierId);

  Optional<Purchase> findPurchaseByIdAndSupplierIdAndStatus(UUID id, UUID supplierId, PurchaseStatus status);

  List<Purchase> findAllPurchases();

  List<Purchase> findAllPurchasesByStatus(PurchaseStatus status);

  List<Purchase> findAllPurchasesBySupplierId(UUID supplierId);

  List<Purchase> findAllPurchasesBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status);

  List<Purchase> findAllPurchasesByItemsProductId(UUID productId);

  List<Purchase> findAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status);

  List<Purchase> findAllPurchasesBySupplierIdAndItemsProductIdAndStatus(UUID supplierId, UUID productId, PurchaseStatus status);

  void deletePurchaseById(UUID id);
}
