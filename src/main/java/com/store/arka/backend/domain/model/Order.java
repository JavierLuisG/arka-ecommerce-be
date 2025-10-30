package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
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
public class Order {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final UUID cartId;
  private final Customer customer;
  private List<OrderItem> items;
  private BigDecimal total;
  private OrderStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Order create(UUID cartId, Customer customer, List<OrderItem> items) {
    if (cartId == null) throw new InvalidArgumentException("CartId in Order cannot be null");
    if (!customer.isActive()) throw new ModelNullException("Customer already deleted previously");
    if (items == null) items = new ArrayList<>();
    return new Order(
        null,
        cartId,
        customer,
        items,
        calculateTotal(items),
        OrderStatus.CREATED,
        null,
        null
    );
  }

  private static BigDecimal calculateTotal(List<OrderItem> items) {
    return items.stream().map(OrderItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void recalculateTotal() {
    this.total = calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
    return items.stream().anyMatch(item -> item.getProductId().equals(productId));
  }

  public void removeOrderItem(Product product) {
    ensureOrderIsModifiable();
    OrderItem found = this.items.stream()
        .filter(orderItem -> orderItem.getProductId().equals(product.getId()))
        .findFirst()
        .orElseThrow(() -> new ModelNotFoundException("Product not found in Order"));
    items.remove(found);
    recalculateTotal();
  }

  public void confirm() {
    ValidateStatusUtils.throwIfNotCreated(this.status);
    if (items.isEmpty()) throw new ItemsEmptyException("Order items cannot be empty to confirm");
    this.status = OrderStatus.CONFIRMED;
  }

  public void pay() {
    ValidateStatusUtils.throwIfNotConfirmed(this.status);
    this.status = OrderStatus.PAID;
  }

  public void shipped() {
    ValidateStatusUtils.throwIfNotPaid(this.status);
    this.status = OrderStatus.SHIPPED;
  }

  public void deliver() {
    ValidateStatusUtils.throwIfNotShipped(this.status);
    this.status = OrderStatus.DELIVERED;
  }

  public void cancel() {
    if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
      throw new InvalidStateException("Cannot cancel an order that is already shipped or delivered");
    }
    this.status = OrderStatus.CANCELED;
  }

  public void ensureOrderIsModifiable() {
    ValidateStatusUtils.throwIfNotCreated(this.status);
  }
}
