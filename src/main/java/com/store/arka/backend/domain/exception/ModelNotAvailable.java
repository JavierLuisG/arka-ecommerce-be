package com.store.arka.backend.domain.exception;

public class ModelNotAvailable extends RuntimeException {
  public ModelNotAvailable(String message) {
    super(message);
  }
}
