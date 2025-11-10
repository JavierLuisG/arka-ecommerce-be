package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.InvalidStateException;
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
public class Payment {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final Order order;
  private final BigDecimal amount;
  private PaymentMethod method;
  private PaymentStatus status;
  private Integer failedAttempts;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime processedAt;
  private static final long MAX_TIME_TO_EXPIRE_MINUTES = 5;
  private static final int MAX_FAILED_ATTEMPTS = 3;

  public static Payment create(Order order, PaymentMethod method) {
    ValidateAttributesUtils.throwIfModelNull(order, "Order in Payment");
    if (!order.isConfirmed()) throw new InvalidStateException("Order must be CONFIRMED to create a payment");
    throwIfBadAmount(order.getTotal());
    return new Payment(
        null,
        order,
        order.getTotal(),
        method,
        PaymentStatus.PENDING,
        0,
        null,
        null,
        null
    );
  }

  public void changeMethod(PaymentMethod method) {
    throwIfProcessed();
    this.method = method;
  }

  public void markPending() {
    throwIfProcessed();
    if (!isFailed()) throw new InvalidStateException("Cannot retry payment unless it is in FAILED state");
    this.status = PaymentStatus.PENDING;
  }

  public void markCompleted() {
    throwIfProcessed();
    if (!isPending()) throw new InvalidStateException("Only pending payments can be completed");
    this.status = PaymentStatus.COMPLETED;
    this.processedAt = LocalDateTime.now();
  }

  public void markFailed() {
    throwIfProcessed();
    this.status = PaymentStatus.FAILED;
    if (failedAttempts < MAX_FAILED_ATTEMPTS) {
      this.failedAttempts += 1;
    }
  }

  public void markExpired() {
    throwIfProcessed();
    this.status = PaymentStatus.EXPIRED;
    this.processedAt = LocalDateTime.now();
  }

  public boolean canRetry() {
    return this.failedAttempts < MAX_FAILED_ATTEMPTS;
  }

  public boolean isExpiredByTime() {
    if (this.createdAt == null) return false;
    return LocalDateTime.now().isAfter(this.createdAt.plusMinutes(MAX_TIME_TO_EXPIRE_MINUTES));
  }


  public boolean amountMismatch() {
    if (!isPending()) throw new InvalidStateException("Only PENDING payments can be completed");
    return this.amount.compareTo(this.order.getTotal()) != 0;
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

  public boolean isExpired() {
    return this.status == PaymentStatus.EXPIRED;
  }

  private void throwIfProcessed() {
    if (isCompleted()) throw new InvalidStateException("Completed payments cannot be changed");
    if (isExpired()) throw new InvalidStateException("Expired payments cannot be changed");
  }

  private static void throwIfBadAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Order total must be greater than zero");
  }
}
