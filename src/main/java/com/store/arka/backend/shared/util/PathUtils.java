package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.exception.InvalidEnumValueException;
import com.store.arka.backend.domain.exception.InvalidIdException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public final class PathUtils {
  public static UUID validateAndParseUUID(String id) {
    if(id == null || id.isBlank()) {
      log.warn("[PATH_UTILS][UUID] Id in controller is required");
      throw new IllegalArgumentException("Id is required");
    }
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException ex) {
      log.warn("[PATH_UTILS][FORMAT] Invalid UUID format {}", id);
      throw new InvalidIdException("Invalid UUID format: " + id);
    }
  }

  public static <E extends Enum<E>> E validateEnumOrThrow(Class<E> enumClass, String status, String model) {
    try {
      return Enum.valueOf(enumClass, status.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      log.warn("[PATH_UTILS][ENUM] Invalid {}:, {}", model, status);
      throw new InvalidEnumValueException("Invalid " + model + ": " + status);
    }
  }
}
