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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseUseCase {
  private final IPurchaseAdapterPort purchaseAdapterPort;
  private final ISupplierUseCase supplierUseCase;
  private final IProductUseCase productUseCase;
  private final IPurchaseItemUseCase purchaseItemUseCase;

  @Override
  @Transactional
  public Purchase createPurchase(Purchase purchase, UUID supplierId) {
    if (purchase == null) throw new ModelNullException("Purchase cannot be null");
    Supplier supplierFound = findSupplierOrThrow(supplierId);
    List<PurchaseItem> purchaseItems = new ArrayList<>();
    purchase.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      purchaseItems.add(PurchaseItem.create(productFound, item.getQuantity(), productFound.getPrice()));
    });
    Purchase created = Purchase.create(supplierFound, purchaseItems);
    return purchaseAdapterPort.saveCreatePurchase(created);
  }

  @Override
  @Transactional
  public Purchase getPurchaseById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return purchaseAdapterPort.findPurchaseById(id)
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Purchase getPurchaseByIdAndStatus(UUID id, PurchaseStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return purchaseAdapterPort.findPurchaseByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public Purchase getPurchaseByIdAndSupplierId(UUID id, UUID supplierId) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findPurchaseByIdAndSupplierId(id, supplierId)
        .orElseThrow(() -> new ModelNotFoundException(
            "Purchase with id " + id + " and supplierId " + supplierId + " not found"));
  }

  @Override
  @Transactional
  public Purchase getPurchaseByIdAndSupplierIdAndStatus(UUID id, UUID supplierId, PurchaseStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findPurchaseByIdAndSupplierIdAndStatus(id, supplierId, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Purchase with id " + id + ", supplierId " + supplierId + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchases() {
    return purchaseAdapterPort.findAllPurchases();
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesByStatus(PurchaseStatus status) {
    return purchaseAdapterPort.findAllPurchasesByStatus(status);
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesBySupplierId(UUID supplierId) {
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findAllPurchasesBySupplierId(supplierId);
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status) {
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findAllPurchasesBySupplierIdAndStatus(supplierId, status);
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    return purchaseAdapterPort.findAllPurchasesByItemsProductId(productId);
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status) {
    findProductOrThrow(productId);
    return purchaseAdapterPort.findAllPurchasesByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional
  public List<Purchase> getAllPurchasesBySupplierIdAndItemsProductIdAndStatus(
      UUID supplierId, UUID productId, PurchaseStatus status) {
    findSupplierOrThrow(supplierId);
    findProductOrThrow(productId);
    return purchaseAdapterPort.findAllPurchasesBySupplierIdAndItemsProductIdAndStatus(supplierId, productId, status);
  }

  @Override
  @Transactional
  public Purchase addPurchaseItemById(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (purchaseFound.containsProduct(productId)) {
      PurchaseItem purchaseItem = findPurchaseItemOrThrow(productId, purchaseFound);
      purchaseItemUseCase.addQuantityById(purchaseItem.getId(), quantity);
      purchaseFound = getPurchaseById(id);
    } else {
      PurchaseItem newItem = PurchaseItem.create(productFound, quantity, BigDecimal.valueOf(1000));
      purchaseFound.getItems().add(newItem);
      purchaseItemUseCase.addPurchaseItem(purchaseFound.getId(), newItem);
    }
    purchaseFound.recalculateTotal();
    return purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
  }

  @Override
  @Transactional
  public Purchase updatePurchaseItemQuantityById(UUID id, UUID productId, Integer quantity) {
    productUseCase.validateAvailabilityOrThrow(productId, quantity);
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (!purchaseFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOperationException("Product not found in Order id " + purchaseFound.getId());
    }
    PurchaseItem purchaseItem = findPurchaseItemOrThrow(productFound.getId(), purchaseFound);
    purchaseItemUseCase.updateQuantity(purchaseItem.getId(), quantity);
    Purchase purchaseUpdated = getPurchaseById(id);
    purchaseUpdated.recalculateTotal();
    return purchaseAdapterPort.saveUpdatePurchase(purchaseUpdated);
  }

  @Override
  @Transactional
  public Purchase removePurchaseItemById(UUID id, UUID productId) {
    Purchase purchaseFound = getPurchaseById(id);
    Product productFound = findProductOrThrow(productId);
    purchaseFound.ensurePurchaseIsModifiable();
    if (!purchaseFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOperationException("Product not found in Purchase id " + purchaseFound.getId());
    }
    purchaseFound.removeOrderItem(productFound);
    return purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
  }

  @Override
  @Transactional
  public void confirmPurchaseById(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.confirm();
    purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
  }

  @Override
  @Transactional
  public void receivePurchaseById(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.receive();
    purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
  }

  @Override
  @Transactional
  public void closePurchaseById(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    purchaseFound.close();
    purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
  }

  @Override
  @Transactional
  public void deletePurchaseById(UUID id) {
    Purchase purchaseFound = getPurchaseById(id);
    throwIfNotConfirmed(purchaseFound);
    purchaseAdapterPort.deletePurchaseById(purchaseFound.getId());
  }

  private static void throwIfNotConfirmed(Purchase purchaseFound) {
    if (purchaseFound.getStatus() != PurchaseStatus.CONFIRMED) {
      throw new InvalidStateException("Purchase CONFIRMED, cannot be modified");
    }
  }

  private Supplier findSupplierOrThrow(UUID supplierId) {
    if (supplierId == null) throw new InvalidArgumentException("SupplierId in Purchase cannot be null");
    return supplierUseCase.getSupplierByIdAndStatus(supplierId, SupplierStatus.ACTIVE);
  }

  private Product findProductOrThrow(UUID productId) {
    if (productId == null) throw new InvalidArgumentException("ProductId in Purchase cannot be null");
    return productUseCase.getProductByIdAndStatus(productId, ProductStatus.ACTIVE);
  }

  private static PurchaseItem findPurchaseItemOrThrow(UUID productId, Purchase purchaseFound) {
    return purchaseFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInOperationException(
            "Product " + productId + " not found in Purchase " + purchaseFound.getId()));
  }
}
