package com.store.arka.backend.domain.exception;

public class PaymentValidationException extends RuntimeException {
  public PaymentValidationException(String message) {
    super(message);
  }
}
