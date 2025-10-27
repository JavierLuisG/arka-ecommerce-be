package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.exception.QuantityBadRequestException;
import com.store.arka.backend.domain.model.Product;

import java.util.UUID;

public final class ValidateAttributesUtils {
  public static void throwIfIdNull(UUID id) {
    if (id == null) {
      throw new InvalidArgumentException("Id is required");
    }
  }

  public static void throwIfProductNull(Product product) {
    if (product == null) {
      throw new ModelNullException("Product cannot be null");
    }
  }

  public static void validateQuantity(Integer quantity) {
    if (quantity == null) {
      throw new InvalidArgumentException("Quantity is required, cannot be null");
    }
    if (quantity <= 0) {
      throw new QuantityBadRequestException("Quantity must be greater than 0");
    }
  }
}
