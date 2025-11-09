package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
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
    ValidateAttributesUtils.throwIfIdNull(cartId, "Cart ID in Order");
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer in Order");
    customer.throwIfDeleted();
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
    if (items == null || items.isEmpty()) return BigDecimal.ZERO;
    return items.stream().map(OrderItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void recalculateTotal() {
    this.total = calculateTotal(items);
  }

  public boolean containsProduct(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Order");
    return items.stream().anyMatch(item -> item.getProductId().equals(productId));
  }

  public void removeOrderItem(Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product in Order");
    ensureOrderIsModifiable();
    OrderItem found = this.items.stream()
        .filter(orderItem -> orderItem.getProductId().equals(product.getId()))
        .findFirst()
        .orElseThrow(() -> new ModelNotFoundException("Product not found in Order"));
    items.remove(found);
    recalculateTotal();
  }

  public void confirm() {
    if (isConfirmed()) throw new InvalidStateException("Order already confirmed");
    ensureOrderIsModifiable();
    if (items.isEmpty()) throw new ItemsEmptyException("Order items cannot be empty to confirm");
    this.status = OrderStatus.CONFIRMED;
  }

  public void pay() {
    if (isPaid()) throw new InvalidStateException("Order already paid");
    ValidateStatusUtils.throwIfNotConfirmed(this.status);
    this.status = OrderStatus.PAID;
  }

  public void shipped() {
    if (isShipped()) throw new InvalidStateException("Order already shipped");
    ValidateStatusUtils.throwIfNotPaid(this.status);
    this.status = OrderStatus.SHIPPED;
  }

  public void deliver() {
    if (isDelivered()) throw new InvalidStateException("Order already delivered");
    ValidateStatusUtils.throwIfNotShipped(this.status);
    this.status = OrderStatus.DELIVERED;
  }

  public void cancel() {
    if (isCanceled()) throw new InvalidStateException("Order already canceled");
    if (isShipped() || isDelivered()) {
      throw new InvalidStateException("Cannot cancel an order that is already shipped or delivered");
    }
    this.status = OrderStatus.CANCELED;
  }

  public boolean isCreated() {
    return this.status == OrderStatus.CREATED;
  }

  public boolean isConfirmed() {
    return this.status == OrderStatus.CONFIRMED;
  }

  public boolean isPaid() {
    return this.status == OrderStatus.PAID;
  }

  public boolean isShipped() {
    return this.status == OrderStatus.SHIPPED;
  }

  public boolean isDelivered() {
    return this.status == OrderStatus.DELIVERED;
  }

  public boolean isCanceled() {
    return this.status == OrderStatus.CANCELED;
  }

  public void ensureOrderIsModifiable() {
    ValidateStatusUtils.throwIfNotCreated(this.status);
  }
}
