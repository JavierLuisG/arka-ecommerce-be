package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.exception.QuantityBadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public final class ValidateAttributesUtils {
  public static void throwIfIdNull(UUID id, String name) {
    if (id == null) {
      log.warn("[VALIDATE_ATTRIBUTES][ID_NULL] ID is required");
      throw new InvalidArgumentException(name + "  is required");
    }
  }

  public static void throwIfModelNull(Object obj, String name) {
    if (obj == null) {
      log.warn("[VALIDATE_ATTRIBUTES][MODEL_NULL] An attempt was made to enter a null value: {}", name);
      throw new ModelNullException(name + " cannot be null");
    }
  }

  public static void validateQuantity(Integer quantity) {
    if (quantity == null) {
      log.warn("[VALIDATE_ATTRIBUTES][VALIDATED_QUANTITY] Quantity in null");
      throw new InvalidArgumentException("Quantity is required, cannot be null");
    }
    if (quantity <= 0) {
      log.warn("[VALIDATE_ATTRIBUTES][VALIDATED_QUANTITY] Quantity lest or equal than 0");
      throw new QuantityBadRequestException("Quantity must be greater than 0");
    }
  }

  public static String throwIfValueNotAllowed(String value, String name) {
    throwIfNullOrEmpty(value, name);
    String normalized = value.trim().toLowerCase();
    List<String> forbiddenNames = List.of("null", "default", "admin");
    if (forbiddenNames.contains(normalized)) {
      log.warn("[VALIDATE_ATTRIBUTES][VALUE_ALLOWED] An attempt was made to enter a wrong value: {}", name);
      throw new InvalidArgumentException("This " + name + " is not allowed");
    }
    return normalized;
  }

  public static String throwIfNullOrEmpty(String value, String field) {
    if (value == null || value.isEmpty()) {
      log.warn("[VALIDATE_ATTRIBUTES][NULL_OR_EMPTY] An attempt was made to enter a null value: {}", field);
      throw new InvalidArgumentException(field + " cannot be null or empty");
    }
    return value.trim();
  }
}
