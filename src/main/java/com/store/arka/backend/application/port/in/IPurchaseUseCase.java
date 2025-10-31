package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.model.Purchase;

import java.util.List;
import java.util.UUID;

public interface IPurchaseUseCase {
  Purchase createPurchase(Purchase purchase, UUID supplierId);

  Purchase getPurchaseById(UUID id);

  Purchase getPurchaseByIdAndStatus(UUID id, PurchaseStatus status);

  Purchase getPurchaseByIdAndSupplierId(UUID id, UUID supplierId);

  Purchase getPurchaseByIdAndSupplierIdAndStatus(UUID id, UUID supplierId, PurchaseStatus status);

  List<Purchase> getAllPurchases();

  List<Purchase> getAllPurchasesByStatus(PurchaseStatus status);

  List<Purchase> getAllPurchasesBySupplierId(UUID supplierId);

  List<Purchase> getAllPurchasesBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status);

  List<Purchase> getAllPurchasesByItemsProductId(UUID productId);

  List<Purchase> getAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status);

  List<Purchase> getAllPurchasesBySupplierIdAndItemsProductIdAndStatus(UUID supplierId, UUID productId, PurchaseStatus status);

  Purchase addPurchaseItemById(UUID id, UUID productId, Integer quantity);

  Purchase updatePurchaseItemQuantityById(UUID id, UUID productId, Integer quantity);

  Purchase removePurchaseItemById(UUID id, UUID productId);

  void confirmPurchaseById(UUID id);

  void receivePurchaseById(UUID id, Purchase purchase);

  void closePurchaseById(UUID id);

  void deletePurchaseById(UUID id);
}
