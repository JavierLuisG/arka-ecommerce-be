package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.exception.InvalidStateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ValidateStatusUtils {
  public static void throwIfCheckout(CartStatus status) {
    if (status == CartStatus.CHECKED_OUT) {
      log.warn("[CART_STATUS][CHECK_OUT] Cart cannot be modified (current: {})", status);
      throw new InvalidStateException("Cart CHECKED_OUT, cannot be modified");
    }
  }

  public static void throwIfNotCreated(OrderStatus status) {
    if (status != OrderStatus.CREATED) {
      log.warn("[ORDER_STATUS][CREATED] Order must be in created state to be modified (current: {})", status);
      throw new InvalidStateException("Order must be in CREATED state to be modified");
    }
  }

  public static void throwIfNotConfirmed(OrderStatus status) {
    if (status != OrderStatus.CONFIRMED) {
      log.warn("[ORDER_STATUS][CONFIRMED] Order must be confirmed to be marked paid (current: {})", status);
      throw new InvalidStateException("Order must be CONFIRMED to be marked PAID");
    }
  }

  public static void throwIfNotPaid(OrderStatus status) {
    if (status != OrderStatus.PAID) {
      log.warn("[ORDER_STATUS][PAID] Order must be paid to be marked shipped (current: {})", status);
      throw new InvalidStateException("Order must be PAID to be marked SHIPPED");
    }
  }

  public static void throwIfNotShipped(OrderStatus status) {
    if (status != OrderStatus.SHIPPED) {
      log.warn("[ORDER_STATUS][SHIPPED] Order must be shipped to be marked delivered (current: {})", status);
      throw new InvalidStateException("Order must be SHIPPED to be marked DELIVERED");
    }
  }
}
