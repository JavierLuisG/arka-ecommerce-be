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
public class OrderItem {
  private final UUID id;
  @EqualsAndHashCode.Include
  private final UUID productId;
  private Product product;
  private Integer quantity;
  private BigDecimal productPrice;
  private BigDecimal subtotal;
  private LocalDateTime createdAt;

  public static OrderItem create(Product product, Integer quantity) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product in OrderItem");
    ValidateAttributesUtils.validateQuantity(quantity);
    return new OrderItem(
        null,
        product.getId(),
        product,
        quantity,
        product.getPrice(),
        product.getPrice().multiply(BigDecimal.valueOf(quantity)),
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
    return productPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public void recalculateSubtotal() {
    this.subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
