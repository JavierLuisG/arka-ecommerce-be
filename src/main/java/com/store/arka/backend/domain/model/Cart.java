package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.CartItemsEmptyException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@AllArgsConstructor
public class Cart {
  private UUID id;
  private final Customer customer;
  private List<CartItem> items;
  private CartStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime abandonedAt;

  public static Cart create(Customer customer, List<CartItem> items) {
    if (!customer.isActive()) {
      throw new ModelNullException("Customer cannot be null or deleted");
    }
    if (items == null) {
      items = new ArrayList<>();
    }
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
    if (items.size() == 0) {
      return BigDecimal.ZERO;
    }
    return items.stream().map(CartItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getCurrentTotal() {
    return calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
    for (CartItem item : items) {
      if (item.getProductId().equals(productId)) {
        return true;
      }
    }
    return false;
  }

  public void removeCartItem(Product product) {
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

  public void confirmed() {
    ValidateStatusUtils.throwIfConfirmed(this.status);
    if (items.isEmpty()) {
      throw new CartItemsEmptyException("Cart items cannot be empty to confirm");
    }
    this.status = CartStatus.CONFIRMED;
  }

  public void abandoned() {
    ValidateStatusUtils.throwIfConfirmed(this.status);
    if (this.status != CartStatus.ABANDONED) {
      this.abandonedAt = LocalDateTime.now();
    }
    this.status = CartStatus.ABANDONED;
  }

  public void ensureCartIsModifiable() {
    ValidateStatusUtils.throwIfConfirmed(this.status);
    markActive();
  }

  private void markActive() {
    if (this.status == CartStatus.ABANDONED) {
      this.status = CartStatus.ACTIVE;
      this.abandonedAt = null;
    }
  }
}
