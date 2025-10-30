package com.store.arka.backend.domain.exception;

public class ProductNotFoundInOperationException extends RuntimeException {
  public ProductNotFoundInOperationException(String message) {
    super(message);
  }
}
