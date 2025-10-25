package com.store.arka.backend.domain.exception;

public class ItemsEmptyException extends RuntimeException {
  public ItemsEmptyException(String message) {
    super(message);
  }
}
