package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.exception.InvalidEnumValueException;
import com.store.arka.backend.domain.exception.InvalidIdException;

import java.util.UUID;

public final class PathUtils {
  public static UUID validateAndParseUUID(String id) {
    if(id == null || id.isBlank()) {
      throw new IllegalArgumentException("Id is required");
    }
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException ex) {
      throw new InvalidIdException("Invalid UUID format: " + id);
    }
  }

  public static <E extends Enum<E>> E validateEnumOrThrow(Class<E> enumClass, String status, String model) {
    try {
      return Enum.valueOf(enumClass, status.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidEnumValueException("Invalid " + model + ": " + status);
    }
  }
}
