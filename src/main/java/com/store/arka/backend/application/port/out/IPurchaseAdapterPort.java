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

  List<Purchase> findAllPurchases();

  List<Purchase> findAllPurchasesByStatus(PurchaseStatus status);

  List<Purchase> findAllPurchasesBySupplierId(UUID supplierId);

  List<Purchase> findAllPurchasesByItemsProductId(UUID productId);

  List<Purchase> findAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status);

  void deletePurchaseById(UUID id);
}
