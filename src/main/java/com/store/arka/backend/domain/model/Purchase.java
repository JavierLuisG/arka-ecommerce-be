package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.exception.*;
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
    if (!supplier.isActive()) throw new ModelDeletionException("Supplier already deleted previously");
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
    return items.stream().map(PurchaseItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void recalculateTotal() {
    this.total = calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
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
    throwIfNotCreated(this.status);
    if (items.isEmpty()) throw new ItemsEmptyException("Purchase items cannot be empty to confirm");
    this.status = PurchaseStatus.CONFIRMED;
  }

  public void reschedule() {
    if (status != PurchaseStatus.CONFIRMED && status != PurchaseStatus.RESCHEDULED) {
      throw new InvalidStateException("Purchase must be CONFIRMED or RESCHEDULED to be marked RECEIVED");
    }
    this.status = PurchaseStatus.RESCHEDULED;
  }

  public void receive() {
    if (status != PurchaseStatus.CONFIRMED && status != PurchaseStatus.RESCHEDULED) {
      throw new InvalidStateException("Purchase must be CONFIRMED or RESCHEDULED to be marked RECEIVED");
    }
    this.status = PurchaseStatus.RECEIVED;
  }

  public void close() {
    if (status != PurchaseStatus.RECEIVED) {
      throw new InvalidStateException("Purchase must be RECEIVED to be marked CLOSED");
    }
    this.status = PurchaseStatus.CLOSED;
  }

  public void ensurePurchaseIsModifiable() {
    throwIfNotCreated(this.status);
  }

  private static void throwIfNotCreated(PurchaseStatus status) {
    if (status != PurchaseStatus.CREATED) {
      throw new InvalidStateException("Purchase must be in CREATED state to be modified");
    }
  }
}
