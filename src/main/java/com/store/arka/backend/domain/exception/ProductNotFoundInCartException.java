package com.store.arka.backend.domain.exception;

public class ProductNotFoundInCartException extends RuntimeException {
  public ProductNotFoundInCartException(String message) {
    super(message);
  }
}
