package com.store.arka.backend.domain.exception;

public class InvalidEnumValueException extends RuntimeException {
  public InvalidEnumValueException(String message) {
    super(message);
  }
}
