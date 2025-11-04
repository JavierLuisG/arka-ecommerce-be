package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPaymentAdapterPort {
  Payment saveCreatePayment(Payment payment);

  Payment saveUpdatePayment(Payment payment);

  Optional<Payment> findPaymentById(UUID id);

  Optional<Payment> findPaymentByOrderId(UUID orderId);

  List<Payment> findAllPayments();

  List<Payment> findAllPaymentsByMethod(PaymentMethod method);

  List<Payment> findAllPaymentsByStatus(PaymentStatus status);

  List<Payment> findAllPaymentsByMethodAndStatus(PaymentMethod method, PaymentStatus status);

  boolean existsPaymentByOrderId(UUID orderId);
}
