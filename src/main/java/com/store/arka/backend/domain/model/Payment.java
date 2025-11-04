package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.InvalidStateException;
import com.store.arka.backend.domain.exception.PaymentValidationException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final Order order;
  private final BigDecimal amount;
  private PaymentMethod method;
  private PaymentStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime processedAt;

  public static Payment create(Order order, PaymentMethod method) {
    if (order == null) throw new IllegalArgumentException("Order must not be null when creating a Payment");
    throwIfBadAmount(order.getTotal());
    return new Payment(
        null,
        order,
        order.getTotal(),
        method,
        PaymentStatus.PENDING,
        null,
        null,
        null
    );
  }

  public void changeMethod(PaymentMethod method) {
    throwIfCompleted();
    this.method = method;
  }

  public void markPending() {
    throwIfCompleted();
    if (!isFailed()) throw new InvalidStateException("Payment must be in FAILED state to retry");
    this.status = PaymentStatus.PENDING;
  }

  public void markCompleted() {
    throwIfCompleted();
    if (!isPending()) throw new InvalidStateException("Payment must be in PENDING state to be marked as COMPLETED");
    this.status = PaymentStatus.COMPLETED;
    this.processedAt = LocalDateTime.now();
  }

  public void validateAmountOrThrow() {
    if (this.amount.compareTo(this.order.getTotal()) != 0) {
      throw new PaymentValidationException("Payment amount " + this.amount + " does not match Order total "
          + this.order.getTotal());
    }
  }

  public void markFailed() {
    throwIfCompleted();
    this.status = PaymentStatus.FAILED;
  }

  private void throwIfCompleted() {
    if (isCompleted()) throw new InvalidStateException("Payment is already COMPLETED and cannot be modified");
  }

  private static void throwIfBadAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Order total must be greater than zero");
  }

  public boolean isCompleted() {
    return this.status == PaymentStatus.COMPLETED;
  }

  public boolean isPending() {
    return this.status == PaymentStatus.PENDING;
  }

  public boolean isFailed() {
    return this.status == PaymentStatus.FAILED;
  }
}
