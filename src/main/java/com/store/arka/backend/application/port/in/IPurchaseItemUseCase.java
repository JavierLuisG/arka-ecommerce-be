package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.model.PurchaseItem;

import java.util.List;
import java.util.UUID;

public interface IPurchaseItemUseCase {
  PurchaseItem addPurchaseItem(UUID purchaseId, PurchaseItem purchaseItem);

  PurchaseItem getPurchaseItemById(UUID id);

  List<PurchaseItem> getAllPurchaseItems();

  List<PurchaseItem> getAllPurchaseItemsByProductId(UUID productId);

  PurchaseItem addQuantityById(UUID id, Integer quantity);

  PurchaseItem updateQuantity(UUID id, Integer quantity);
}
