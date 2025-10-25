package com.store.arka.backend.domain.exception;

public class ProductNotFoundInOrderException extends RuntimeException {
  public ProductNotFoundInOrderException(String message) {
    super(message);
  }
}
