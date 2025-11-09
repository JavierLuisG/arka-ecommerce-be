package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Purchase {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final Supplier supplier;
  private List<PurchaseItem> items;
  private BigDecimal total;
  private PurchaseStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Purchase create(Supplier supplier, List<PurchaseItem> items) {
    ValidateAttributesUtils.throwIfModelNull(supplier, "Supplier in Purchase");
    supplier.throwIfDeleted();
    if (items == null) items = new ArrayList<>();
    return new Purchase(
        null,
        supplier,
        items,
        calculateTotal(items),
        PurchaseStatus.CREATED,
        null,
        null
    );
  }

  private static BigDecimal calculateTotal(List<PurchaseItem> items) {
    if (items == null || items.isEmpty()) return BigDecimal.ZERO;
    return items.stream().map(PurchaseItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void recalculateTotal() {
    this.total = calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Purchase");
    return items.stream().anyMatch(item -> item.getProductId().equals(productId));
  }

  public void addPurchaseItem(Product product, Integer quantity, BigDecimal unitCost) {
    ensurePurchaseIsModifiable();
    items.stream()
        .filter(purchaseItem -> purchaseItem.getProductId().equals(product.getId()))
        .findFirst()
        .ifPresentOrElse(
            purchaseItem -> purchaseItem.addQuantity(quantity),
            () -> {
              PurchaseItem created = PurchaseItem.create(product, quantity, unitCost);
              items.add(created);
            }
        );
    recalculateTotal();
  }

  public void updatePurchaseItem(Product product, Integer quantity) {
    ensurePurchaseIsModifiable();
    items.stream()
        .filter(purchaseItem -> purchaseItem.getProductId().equals(product.getId()))
        .findFirst()
        .ifPresentOrElse(purchaseItem -> {
              purchaseItem.updateQuantity(quantity);
              recalculateTotal();
            },
            () -> {
              throw new ModelNotFoundException("Product not found in Purchase");
            }
        );
  }

  public void removePurchaseItem(Product product) {
    ensurePurchaseIsModifiable();
    PurchaseItem found = this.items.stream()
        .filter(purchaseItem -> purchaseItem.getProductId().equals(product.getId()))
        .findFirst()
        .orElseThrow(() -> new ModelNotFoundException("Product not found in Purchase"));
    items.remove(found);
    recalculateTotal();
  }

  public void confirm() {
    if (isConfirmed()) throw new InvalidStateException("Purchase already confirmed");
    ensurePurchaseIsModifiable();
    if (items.isEmpty()) throw new ItemsEmptyException("Purchase items cannot be empty to confirm");
    this.status = PurchaseStatus.CONFIRMED;
  }

  public void reschedule() {
    if (isRescheduled()) throw new InvalidStateException("Purchase already rescheduled, It must be complete to be received");
    if (!isConfirmed()) {
      throw new InvalidStateException("Purchase must be CONFIRMED or RESCHEDULED to be marked RECEIVED");
    }
    this.status = PurchaseStatus.RESCHEDULED;
  }

  public void receive() {
    if (!isConfirmed() && !isRescheduled()) {
      throw new InvalidStateException("Purchase must be CONFIRMED or RESCHEDULED to be marked RECEIVED");
    }
    this.status = PurchaseStatus.RECEIVED;
  }

  public void close() {
    if (isClosed()) throw new InvalidStateException("Purchase already closed");
    if (!isReceived()) throw new InvalidStateException("Purchase must be RECEIVED to be marked CLOSED");
    this.status = PurchaseStatus.CLOSED;
  }

  public boolean isCreated() {
    return this.status == PurchaseStatus.CREATED;
  }

  public boolean isConfirmed() {
    return this.status == PurchaseStatus.CONFIRMED;
  }

  public boolean isReceived() {
    return this.status == PurchaseStatus.RECEIVED;
  }

  public boolean isRescheduled() {
    return this.status == PurchaseStatus.RESCHEDULED;
  }

  public boolean isClosed() {
    return this.status == PurchaseStatus.CLOSED;
  }

  public void ensurePurchaseIsModifiable() {
    if (!isCreated()) throw new InvalidStateException("Purchase must be in CREATED state to be modified");
  }
}
