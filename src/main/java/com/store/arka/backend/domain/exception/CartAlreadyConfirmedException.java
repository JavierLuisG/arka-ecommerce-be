package com.store.arka.backend.domain.exception;

public class CartAlreadyConfirmedException extends RuntimeException {
  public CartAlreadyConfirmedException(String message) {
    super(message);
  }
}
