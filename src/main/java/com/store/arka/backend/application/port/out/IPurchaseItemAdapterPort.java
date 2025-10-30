package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.model.PurchaseItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPurchaseItemAdapterPort {
  PurchaseItem saveAddPurchaseItem(UUID purchaseId, PurchaseItem PurchaseItem);

  PurchaseItem saveUpdatePurchaseItem(PurchaseItem purchaseId);

  Optional<PurchaseItem> findPurchaseItemById(UUID id);

  List<PurchaseItem> findAllPurchaseItems();

  List<PurchaseItem> findAllPurchaseItemsByProductId(UUID productId);
}
