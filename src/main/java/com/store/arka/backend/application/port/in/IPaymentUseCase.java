package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.model.Payment;

import java.util.List;
import java.util.UUID;

public interface IPaymentUseCase {
  Payment createPayment(UUID orderId, Payment payment);

  Payment getPaymentById(UUID id);

  Payment getPaymentByIdSecure(UUID id);

  Payment getPaymentByOrderId(UUID orderId);

  List<Payment> getAllPaymentsByFilters(String method, String status);

  Payment confirmPayment(UUID id);

  Payment changePaymentMethod(UUID id, PaymentMethod method);

  Payment payAgain(UUID id);
}
