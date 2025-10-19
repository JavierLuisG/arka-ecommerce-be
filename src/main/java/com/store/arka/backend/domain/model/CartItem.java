package com.store.arka.backend.domain.model;

import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {
  private final UUID id;
  @EqualsAndHashCode.Include
  private final UUID productId;
  private final Product product;
  private Integer quantity;
  private LocalDateTime addedAt;

  public static CartItem create(Product product, Integer quantity) {
    ValidateAttributesUtils.throwIfProductNull(product);
    ValidateAttributesUtils.validateQuantity(quantity);
    return new CartItem(
        null,
        product.getId(),
        product,
        quantity,
        null
    );
  }

  public void addQuantity(Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    this.quantity += quantity;
  }

  public void updateQuantity(Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    this.quantity = quantity;
  }

  public BigDecimal calculateSubtotal() {
    return product.getPrice().multiply(BigDecimal.valueOf(quantity));
  }
}
