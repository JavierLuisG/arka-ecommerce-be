package com.store.arka.backend.domain.exception;

public class CartItemsEmptyException extends RuntimeException {
  public CartItemsEmptyException(String message) {
    super(message);
  }
}
