package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.exception.InvalidStateException;

public final class ValidateStatusUtils {
  public static void throwIfCheckedOut(CartStatus status) {
    if (status == CartStatus.CHECKED_OUT) {
      throw new InvalidStateException("Cart CHECKED_OUT, cannot be modified");
    }
  }

  public static void throwIfNotCreated(OrderStatus status) {
    if (status != OrderStatus.CREATED) {
      throw new InvalidStateException("Order must be in CREATED state to be modified");
    }
  }

  public static void throwIfNotConfirmed(OrderStatus status) {
    if (status != OrderStatus.CONFIRMED) {
      throw new InvalidStateException("Order must be CONFIRMED to be marked PAID");
    }
  }

  public static void throwIfNotPaid(OrderStatus status) {
    if (status != OrderStatus.PAID) {
      throw new InvalidStateException("Order must be PAID to be marked SHIPPED");
    }
  }

  public static void throwIfNotShipped(OrderStatus status) {
    if (status != OrderStatus.SHIPPED) {
      throw new InvalidStateException("Order must be SHIPPED to be marked DELIVERED");
    }
  }
}
