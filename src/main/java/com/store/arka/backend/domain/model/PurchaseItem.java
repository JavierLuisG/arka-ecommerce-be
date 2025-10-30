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
public class PurchaseItem {
  private final UUID id;
  @EqualsAndHashCode.Include
  private final UUID productId;
  private Product product;
  private Integer quantity;
  private BigDecimal unitCost;
  private BigDecimal subtotal;
  private LocalDateTime createdAt;

  public static PurchaseItem create(Product product, Integer quantity, BigDecimal unitCost) {
    return new PurchaseItem(
        null,
        product.getId(),
        product,
        quantity,
        unitCost,
        unitCost.multiply(BigDecimal.valueOf(quantity)),
        null
    );
  }

  public void addQuantity(Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    this.quantity += quantity;
    recalculateSubtotal();
  }

  public void updateQuantity(Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    this.quantity = quantity;
    recalculateSubtotal();
  }

  public BigDecimal calculateSubtotal() {
    return unitCost.multiply(BigDecimal.valueOf(quantity));
  }

  public void recalculateSubtotal() {
    this.subtotal = unitCost.multiply(BigDecimal.valueOf(quantity));
  }
}
