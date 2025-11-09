package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.ItemsEmptyException;
import com.store.arka.backend.domain.exception.InvalidStateException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cart {
  @EqualsAndHashCode.Include
  private UUID id;
  private final Customer customer;
  private List<CartItem> items;
  private CartStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime abandonedAt;

  public static Cart create(Customer customer, List<CartItem> items) {
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer in Cart");
    customer.throwIfDeleted();
    if (items == null) items = new ArrayList<>();
    return new Cart(
        null,
        customer,
        items,
        CartStatus.ACTIVE,
        null,
        null,
        null
    );
  }

  public static BigDecimal calculateTotal(List<CartItem> items) {
    if (items == null || items.isEmpty()) return BigDecimal.ZERO;
    return items.stream().map(CartItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getCurrentTotal() {
    return calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Cart");
    return items.stream().anyMatch(item -> item.getProductId().equals(productId));
  }

  public void removeCartItem(Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product in Cart");
    ensureCartIsModifiable();
    CartItem found = this.items.stream()
        .filter(cartItem -> cartItem.getProductId().equals(product.getId()))
        .findFirst()
        .orElseThrow(() -> new ModelNotFoundException("Product not found in Cart"));
    items.remove(found);
  }

  public void emptyCartItems() {
    ensureCartIsModifiable();
    this.items.clear();
  }

  public void checkout() {
    ensureCartIsModifiable();
    if (items.isEmpty()) throw new ItemsEmptyException("Cart items cannot be empty to checkout");
    if (!isActive()) throw new InvalidStateException("Cart must be active to checkout");
    this.status = CartStatus.CHECKED_OUT;
  }

  public void abandon() {
    if (isAbandoned()) throw new InvalidStateException("Cart already abandoned");
    ensureCartIsModifiable();
    this.abandonedAt = LocalDateTime.now();
    this.status = CartStatus.ABANDONED;
  }

  public boolean isActive() {
    return this.status == CartStatus.ACTIVE;
  }

  public boolean isCheckout() {
    return this.status == CartStatus.CHECKED_OUT;
  }

  public boolean isAbandoned() {
    return this.status == CartStatus.ABANDONED;
  }

  private void markActiveIfAbandoned() {
    if (isAbandoned()) {
      this.status = CartStatus.ACTIVE;
      this.abandonedAt = null;
    }
  }

  public void ensureCartIsModifiable() {
    ValidateStatusUtils.throwIfCheckout(this.status);
    markActiveIfAbandoned();
  }
}
