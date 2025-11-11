package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.in.IPurchaseItemUseCase;
import com.store.arka.backend.application.port.out.IPurchaseItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.shared.security.SecurityUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseItemService implements IPurchaseItemUseCase {
  private final IPurchaseItemAdapterPort purchaseItemAdapterPort;
  private final IProductUseCase productUseCase;
  private final SecurityUtils securityUtils;

  @Override
  public PurchaseItem addPurchaseItem(UUID purchaseId, PurchaseItem purchaseItem) {
    ValidateAttributesUtils.throwIfIdNull(purchaseId, "Purchase ID in PurchaseItem");
    ValidateAttributesUtils.throwIfModelNull(purchaseItem, "PurchaseItem");
    productUseCase.validateAvailability(purchaseItem.getProductId(), purchaseItem.getQuantity());
    PurchaseItem saved = purchaseItemAdapterPort.saveAddPurchaseItem(purchaseId, purchaseItem);
    log.info("[PURCHASE_ITEM_SERVICE][CREATED] User(id={}) has created new PurchaseItem(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public PurchaseItem getPurchaseItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "PurchaseItem ID");
    return purchaseItemAdapterPort.findPurchaseItemById(id)
        .orElseThrow(() -> {
          log.warn("[PURCHASE_ITEM_SERVICE][GET_BY_ID] PurchaseItem(id={}) not found", id);
          return new ModelNotFoundException("PurchaseItem ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<PurchaseItem> getAllPurchaseItems() {
    log.info("[PURCHASE_ITEM_SERVICE][GET_ALL] Fetching all PurchaseItems");
    return purchaseItemAdapterPort.findAllPurchaseItems();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PurchaseItem> getAllPurchaseItemsByProductId(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in PurchaseItem");
    log.info("[PURCHASE_ITEM_SERVICE][GET_ALL_BY_PRODUCT] Fetching all PurchaseItems with Product(id={})", productId);
    return purchaseItemAdapterPort.findAllPurchaseItemsByProductId(productId);
  }

  @Override
  public PurchaseItem addQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    PurchaseItem found = getPurchaseItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailability(found.getProductId(), found.getQuantity());
    PurchaseItem saved = purchaseItemAdapterPort.saveUpdatePurchaseItem(found);
    log.info("[PURCHASE_ITEM_SERVICE][ADDED_QUANTITY] User(id={}) has added quantity {} in PurchaseItem(id={})",
        securityUtils.getCurrentUserId(), quantity, found.getId());
    return saved;
  }

  @Override
  public PurchaseItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    PurchaseItem found = getPurchaseItemById(id);
    found.updateQuantity(quantity);
    PurchaseItem saved = purchaseItemAdapterPort.saveUpdatePurchaseItem(found);
    log.info("[PURCHASE_ITEM_SERVICE][UPDATED_QUANTITY] User(id={}) has updated quantity {} in PurchaseItem(id={})",
        securityUtils.getCurrentUserId(), quantity, found.getId());
    return saved;
  }
}
