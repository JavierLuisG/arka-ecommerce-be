package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.model.Payment;

import java.util.List;
import java.util.UUID;

public interface IPaymentUseCase {
  Payment createPayment(UUID orderId, Payment payment);

  Payment getPaymentById(UUID id);

  Payment getPaymentByOrderId(UUID orderId);

  List<Payment> getAllPayments();

  List<Payment> getAllPaymentsByMethod(PaymentMethod method);

  List<Payment> getAllPaymentsByStatus(PaymentStatus status);

  List<Payment> getAllPaymentsByMethodAndStatus(PaymentMethod method, PaymentStatus status);

  Payment confirmPaymentById(UUID id);

  Payment changePaymentMethodById(UUID id, PaymentMethod method);

  Payment payAgainById(UUID id);

  boolean existsPaymentByOrderId(UUID orderId);
}
