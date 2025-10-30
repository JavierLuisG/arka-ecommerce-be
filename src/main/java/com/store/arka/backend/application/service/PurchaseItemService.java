package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.in.IPurchaseItemUseCase;
import com.store.arka.backend.application.port.out.IPurchaseItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseItemService implements IPurchaseItemUseCase {
  private final IPurchaseItemAdapterPort purchaseItemAdapterPort;
  private final IProductUseCase productUseCase;

  @Override
  public PurchaseItem addPurchaseItem(UUID purchaseId, PurchaseItem purchaseItem) {
    if (purchaseItem == null) throw new ModelNullException("PurchaseItem cannot be null");
    productUseCase.validateAvailabilityOrThrow(purchaseItem.getProductId(), purchaseItem.getQuantity());
    return purchaseItemAdapterPort.saveAddPurchaseItem(purchaseId, purchaseItem);
  }

  @Override
  @Transactional
  public PurchaseItem getPurchaseItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return purchaseItemAdapterPort.findPurchaseItemById(id)
        .orElseThrow(() -> new ModelNotFoundException("PurchaseItem with id " + id + " not found"));
  }

  @Override
  @Transactional
  public List<PurchaseItem> getAllPurchaseItems() {
    return purchaseItemAdapterPort.findAllPurchaseItems();
  }

  @Override
  @Transactional
  public List<PurchaseItem> getAllPurchaseItemsByProductId(UUID productId) {
    return purchaseItemAdapterPort.findAllPurchaseItemsByProductId(productId);
  }

  @Override
  public PurchaseItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    PurchaseItem found = getPurchaseItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    return purchaseItemAdapterPort.saveUpdatePurchaseItem(found);
  }

  @Override
  public PurchaseItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    PurchaseItem found = getPurchaseItemById(id);
    found.updateQuantity(quantity);
    return purchaseItemAdapterPort.saveUpdatePurchaseItem(found);
  }
}
