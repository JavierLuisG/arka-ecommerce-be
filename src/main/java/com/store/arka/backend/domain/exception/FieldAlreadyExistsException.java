package com.store.arka.backend.domain.exception;

public class FieldAlreadyExistsException extends RuntimeException {
  public FieldAlreadyExistsException(String message) {
    super(message);
  }
}
