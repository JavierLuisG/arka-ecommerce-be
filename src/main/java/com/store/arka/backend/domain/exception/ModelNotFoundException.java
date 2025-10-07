package com.store.arka.backend.domain.exception;

public class ModelNotFoundException extends RuntimeException {
  public ModelNotFoundException(String message) {
    super(message);
  }
}
