package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.in.IPurchaseItemUseCase;
import com.store.arka.backend.application.port.in.IPurchaseUseCase;
import com.store.arka.backend.application.port.in.ISupplierUseCase;
import com.store.arka.backend.application.port.out.IPurchaseAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseUseCase {
  private final IPurchaseAdapterPort purchaseAdapterPort;
  private final PurchaseReschedulerService reschedulerService;
  private final ISupplierUseCase supplierUseCase;
  private final IProductUseCase productUseCase;
  private final IPurchaseItemUseCase purchaseItemUseCase;

  @Override
  @Transactional
  public Purchase createPurchase(Purchase purchase, UUID supplierId) {
    ValidateAttributesUtils.throwIfModelNull(purchase, "Purchase");
    Supplier supplierFound = findSupplierOrThrow(supplierId);
    List<PurchaseItem> purchaseItems = new ArrayList<>();
    purchase.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      purchaseItems.add(PurchaseItem.create(productFound, item.getQuantity(), item.getUnitCost()));
    });
    Purchase created = Purchase.create(supplierFound, purchaseItems);
    Purchase saved = purchaseAdapterPort.saveCreatePurchase(created);
    log.info("[PURCHASE_SERVICE][CREATED] Created new purchase ID {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Purchase getPurchaseById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Purchase ID");
    return purchaseAdapterPort.findPurchaseById(id)
        .orElseThrow(() -> {
          log.warn("[PURCHASE_SERVICE][GET_BY_ID] Purchase ID {} not found", id);
          return new ModelNotFoundException("Purchase ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchases() {
    log.info("[PURCHASE_SERVICE][GET_ALL] Fetching all purchases");
    return purchaseAdapterPort.findAllPurchases();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesByStatus(PurchaseStatus status) {
    log.info("[PURCHASE_SERVICE][GET_ALL_BY_STATUS] Fetching all purchases with status {}", status);
    return purchaseAdapterPort.findAllPurchasesByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesBySupplierId(UUID supplierId) {
    findSupplierOrThrow(supplierId);
    log.info("[PURCHASE_SERVICE][GET_ALL_BY_SUPPLIER] Fetching all purchases with supplier ID {}", supplierId);
    return purchaseAdapterPort.findAllPurchasesBySupplierId(supplierId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    log.info("[PURCHASE_SERVICE][GET_ALL_BY_PRODUCT] Fetching all purchases with product ID {}", productId);
    return purchaseAdapterPort.findAllPurchasesByItemsProductId(productId);
  }

  @Override
  @Transactional
  public Purchase addPurchaseItem(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (purchaseFound.containsProduct(productId)) {
      PurchaseItem purchaseItem = findPurchaseItemOrThrow(productId, purchaseFound);
      purchaseItemUseCase.addQuantityById(purchaseItem.getId(), quantity);
      purchaseFound = getPurchaseById(id);
      log.info("[PURCHASE_SERVICE][ADDED_ITEM] Add quantity {} in item ID {}", quantity, purchaseItem.getId());
    } else {
      PurchaseItem newItem = PurchaseItem.create(productFound, quantity, BigDecimal.valueOf(1000));
      purchaseFound.getItems().add(newItem);
      PurchaseItem saved = purchaseItemUseCase.addPurchaseItem(purchaseFound.getId(), newItem);
      log.info("[PURCHASE_SERVICE][ADDED_ITEM] Create item ID {} whit product ID {} in purchase ID {}",
          saved.getId(), productId, id);
    }
    purchaseFound.recalculateTotal();
    Purchase saved = purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
    log.info("[PURCHASE_SERVICE][ADDED_ITEM] Updated purchase ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Purchase updatePurchaseItemQuantity(UUID id, UUID productId, Integer quantity) {
    productUseCase.validateAvailability(productId, quantity);
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (!purchaseFound.containsProduct(productFound.getId())) {
      log.warn("[PURCHASE_SERVICE][UPDATED_ITEM_QUANTITY] Product ID {} not found in purchase ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Purchase ID " + purchaseFound.getId());
    }
    PurchaseItem purchaseItem = findPurchaseItemOrThrow(productFound.getId(), purchaseFound);
    purchaseItemUseCase.updateQuantity(purchaseItem.getId(), quantity);
    Purchase purchaseUpdated = getPurchaseById(id);
    purchaseUpdated.recalculateTotal();
    Purchase saved = purchaseAdapterPort.saveUpdatePurchase(purchaseUpdated);
    log.info("[PURCHASE_SERVICE][UPDATED_ITEM_QUANTITY] Updated quantity {} in item ID {} ", quantity, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Purchase removePurchaseItem(UUID id, UUID productId) {
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (!purchaseFound.containsProduct(productFound.getId())) {
      log.warn("[PURCHASE_SERVICE][REMOVED_ITEM] Product ID {} not found in purchase ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Purchase ID " + purchaseFound.getId());
    }
    purchaseFound.removePurchaseItem(productFound);
    Purchase saved = purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
    log.info("[PURCHASE_SERVICE][REMOVED_ITEM] Product ID {} has removed of purchase ID {}", productId, id);
    return saved;
  }

  @Override
  @Transactional
  public void confirmPurchase(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.confirm();
    purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
    log.info("[PURCHASE_SERVICE][CONFIRMED] Purchase ID {} was confirmed", id);
  }

  @Override
  @Transactional
  public void receivePurchase(UUID id, Purchase receivedPurchase) {
    Purchase purchaseFound = getPurchaseById(id);
    try {
      validateReceivedPurchase(receivedPurchase, purchaseFound);
      receivedPurchase.getItems().forEach(item -> {
        productUseCase.increaseStock(item.getProductId(), item.getQuantity());
        log.info("[PURCHASE_SERVICE][RECEIVED] Increase stock {} in product ID {}",
            item.getQuantity(), item.getProductId());
      });
      purchaseFound.receive();
      purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
      log.info("[PURCHASE_SERVICE][RECEIVED] Purchase ID {} was confirmed", id);
    } catch (InvalidArgumentException | InvalidStateException ex) {
      reschedulerService.markPurchaseAsRescheduled(purchaseFound);
      log.error("[PURCHASE_SERVICE][RECEIVED] Error receiving purchase {}: {}", id, ex.getMessage());
      throw ex;
    }
  }

  @Override
  @Transactional
  public void closePurchase(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.close();
    purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
    log.info("[PURCHASE_SERVICE][CLOSED] Purchase ID {} was closed", id);
  }

  @Override
  @Transactional
  public void deletePurchase(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.ensurePurchaseIsModifiable();
    purchaseAdapterPort.deletePurchaseById(purchaseFound.getId());
    log.info("[PURCHASE_SERVICE][DELETED] Purchase ID {} was deleted", id);
  }

  private Supplier findSupplierOrThrow(UUID supplierId) {
    ValidateAttributesUtils.throwIfIdNull(supplierId, "Supplier ID in Purchase");
    Supplier supplier = supplierUseCase.getSupplierById(supplierId);
    supplier.throwIfDeleted();
    return supplier;
  }

  private Product findProductOrThrow(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Purchase");
    Product product = productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private PurchaseItem findPurchaseItemOrThrow(UUID productId, Purchase purchaseFound) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Purchase");
    return purchaseFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> {
          log.warn("[PURCHASE_SERVICE][FOUND_ITEM] Product ID {} not found in purchase {}",
              productId, purchaseFound.getId());
          return new ProductNotFoundInOperationException("Product " + productId + " not found in Purchase " + purchaseFound.getId());
        });
  }

  private void validateReceivedPurchase(Purchase receivedPurchase, Purchase purchaseFound) {
    if (purchaseFound.getItems().size() != receivedPurchase.getItems().size()) {
      log.warn("[PURCHASE_SERVICE][RECEIVED] Number of products does not match {}}", purchaseFound.getId());
      throw new InvalidArgumentException("Number of products does not match original purchase");
    }
    receivedPurchase.getItems().forEach(item -> {
      PurchaseItem itemFound = purchaseFound.getItems().stream()
          .filter(purchaseItem -> purchaseItem.getProductId().equals(item.getProductId()))
          .findFirst()
          .orElseThrow(() -> {
            log.warn("[PURCHASE_SERVICE][RECEIVED] Product ID {} not found in {}", item.getProduct().getId(), purchaseFound.getId());
            return new InvalidArgumentException("Product " + item.getProduct().getName() + " not found in original purchase");
          });
      if (!itemFound.hasCorrectQuantity(item.getQuantity())) {
        log.warn("[PURCHASE_SERVICE][RECEIVED] Quantity mismatch in product ID {}", itemFound.getProduct().getId());
        throw new InvalidArgumentException(String.format(
            "Quantity mismatch for product %s (expected %d, received %d)",
            itemFound.getProduct().getName(),
            itemFound.getQuantity(),
            item.getQuantity()
        ));
      }
    });
  }
}
