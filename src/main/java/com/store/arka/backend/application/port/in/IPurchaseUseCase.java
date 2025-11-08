package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.model.Purchase;

import java.util.List;
import java.util.UUID;

public interface IPurchaseUseCase {
  Purchase createPurchase(Purchase purchase, UUID supplierId);

  Purchase getPurchaseById(UUID id);

  List<Purchase> getAllPurchases();

  List<Purchase> getAllPurchasesByStatus(PurchaseStatus status);

  List<Purchase> getAllPurchasesBySupplierId(UUID supplierId);

  List<Purchase> getAllPurchasesByItemsProductId(UUID productId);

  Purchase addPurchaseItem(UUID id, UUID productId, Integer quantity);

  Purchase updatePurchaseItemQuantity(UUID id, UUID productId, Integer quantity);

  Purchase removePurchaseItem(UUID id, UUID productId);

  void confirmPurchase(UUID id);

  void receivePurchase(UUID id, Purchase purchase);

  void closePurchase(UUID id);

  void deletePurchase(UUID id);
}
