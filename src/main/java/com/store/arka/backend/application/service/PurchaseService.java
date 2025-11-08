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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    if (purchase == null) throw new ModelNullException("Purchase cannot be null");
    Supplier supplierFound = findSupplierOrThrow(supplierId);
    List<PurchaseItem> purchaseItems = new ArrayList<>();
    purchase.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      purchaseItems.add(PurchaseItem.create(productFound, item.getQuantity(), item.getUnitCost()));
    });
    Purchase created = Purchase.create(supplierFound, purchaseItems);
    return purchaseAdapterPort.saveCreatePurchase(created);
  }

  @Override
  @Transactional(readOnly = true)
  public Purchase getPurchaseById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return purchaseAdapterPort.findPurchaseById(id)
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Purchase getPurchaseByIdAndStatus(UUID id, PurchaseStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return purchaseAdapterPort.findPurchaseByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Purchase with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Purchase getPurchaseByIdAndSupplierId(UUID id, UUID supplierId) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findPurchaseByIdAndSupplierId(id, supplierId)
        .orElseThrow(() -> new ModelNotFoundException(
            "Purchase with id " + id + " and supplierId " + supplierId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Purchase getPurchaseByIdAndSupplierIdAndStatus(UUID id, UUID supplierId, PurchaseStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findPurchaseByIdAndSupplierIdAndStatus(id, supplierId, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Purchase with id " + id + ", supplierId " + supplierId + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchases() {
    return purchaseAdapterPort.findAllPurchases();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesByStatus(PurchaseStatus status) {
    return purchaseAdapterPort.findAllPurchasesByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesBySupplierId(UUID supplierId) {
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findAllPurchasesBySupplierId(supplierId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesBySupplierIdAndStatus(UUID supplierId, PurchaseStatus status) {
    findSupplierOrThrow(supplierId);
    return purchaseAdapterPort.findAllPurchasesBySupplierIdAndStatus(supplierId, status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    return purchaseAdapterPort.findAllPurchasesByItemsProductId(productId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Purchase> getAllPurchasesByItemsProductIdAndStatus(UUID productId, PurchaseStatus status) {
    findProductOrThrow(productId);
    return purchaseAdapterPort.findAllPurchasesByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional(readOnly = true)
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
    purchaseFound.removePurchaseItem(productFound);
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
  public void receivePurchaseById(UUID id, Purchase receivedPurchase) {
    Purchase purchaseFound = getPurchaseById(id);
    try {
      validateReceivedPurchase(receivedPurchase, purchaseFound);
      receivedPurchase.getItems().forEach(item ->
          productUseCase.increaseStock(item.getProductId(), item.getQuantity()));
      purchaseFound.receive();
      purchaseAdapterPort.saveUpdatePurchase(purchaseFound);
    } catch (BusinessException ex) {
      reschedulerService.markPurchaseAsRescheduled(purchaseFound);
      throw ex;
    }
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
    purchaseFound.ensurePurchaseIsModifiable();
    purchaseAdapterPort.deletePurchaseById(purchaseFound.getId());
  }

  private Supplier findSupplierOrThrow(UUID supplierId) {
    if (supplierId == null) throw new InvalidArgumentException("SupplierId in Purchase cannot be null");
    Supplier supplier = supplierUseCase.getSupplierById(supplierId);
    supplier.throwIfDeleted();
    return supplier;
  }

  private Product findProductOrThrow(UUID productId) {
    if (productId == null) throw new InvalidArgumentException("ProductId in Purchase cannot be null");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private static PurchaseItem findPurchaseItemOrThrow(UUID productId, Purchase purchaseFound) {
    return purchaseFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInOperationException(
            "Product " + productId + " not found in Purchase " + purchaseFound.getId()));
  }

  private static void validateReceivedPurchase(Purchase receivedPurchase, Purchase purchaseFound) {
    if (purchaseFound.getItems().size() != receivedPurchase.getItems().size()) {
      throw new InvalidArgumentException("Number of products does not match original purchase");
    }
    receivedPurchase.getItems().forEach(item -> {
      PurchaseItem itemFound = purchaseFound.getItems().stream()
          .filter(purchaseItem -> purchaseItem.getProductId().equals(item.getProductId()))
          .findFirst()
          .orElseThrow(() -> new InvalidArgumentException(
              "Product " + item.getProduct().getName() + " not found in original purchase"));
      if (!itemFound.hasCorrectQuantity(item.getQuantity())) {
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
