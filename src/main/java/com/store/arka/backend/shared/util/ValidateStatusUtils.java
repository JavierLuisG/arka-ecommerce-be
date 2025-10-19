package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.CartAlreadyConfirmedException;

public final class ValidateStatusUtils {
  public static void throwIfConfirmed(CartStatus status) {
    if (status == CartStatus.CONFIRMED) {
      throw new CartAlreadyConfirmedException("Cart CONFIRMED, cannot be modified");
    }
  }
}
