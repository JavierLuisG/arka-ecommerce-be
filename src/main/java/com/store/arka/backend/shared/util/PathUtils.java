package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.exception.InvalidEnumValueException;
import com.store.arka.backend.domain.exception.InvalidIdException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public final class PathUtils {
  public static UUID validateAndParseUUID(String id) {
    if(id == null || id.isBlank()) {
      log.warn("[PATH_UTILS][VALIDATE_AND_PARSE_UUID] Id is required");
      throw new InvalidIdException("ID is required");
    }
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException ex) {
      log.warn("[PATH_UTILS][VALIDATE_AND_PARSE_UUID] Invalid UUID format {}", id);
      throw new InvalidIdException("Invalid UUID format");
    }
  }

  public static <E extends Enum<E>> E validateEnumOrThrow(Class<E> enumClass, String value, String model) {
    if(value == null) {
      log.warn("[PATH_UTILS][VALIDATE_ENUM_OR_THROW] {} in controller is required", model);
      throw new IllegalArgumentException(model + " is required");
    }
    try {
      return Enum.valueOf(enumClass, value.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      log.warn("[PATH_UTILS][VALIDATE_ENUM_OR_THROW] Invalid {}:, {}", model, value);
      throw new InvalidEnumValueException("Invalid " + model + ": " + value);
    }
  }
}
