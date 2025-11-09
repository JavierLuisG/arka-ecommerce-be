package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IPaymentUseCase;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.application.port.out.IOrderPaymentSyncPort;
import com.store.arka.backend.application.port.out.IPaymentAdapterPort;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentUseCase {
  private final IPaymentAdapterPort paymentAdapterPort;
  private final IOrderAdapterPort orderAdapterPort;
  private final IOrderPaymentSyncPort orderPaymentSyncPort;

  @Override
  @Transactional
  public Payment createPayment(UUID orderId, Payment payment) {
    Order orderFound = requireOrderConfirmed(orderId);
    if (paymentAdapterPort.existsPaymentByOrderId(orderId)) {
      throw new FieldAlreadyExistsException("A payment already exists for Order ID " + orderId);
    }
    Payment created = Payment.create(orderFound, payment.getMethod());
    return paymentAdapterPort.saveCreatePayment(created);
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Payment ID");
    return paymentAdapterPort.findPaymentById(id)
        .orElseThrow(() -> new ModelNotFoundException("Payment not found for ID " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    return paymentAdapterPort.findPaymentByOrderId(found.getId())
        .orElseThrow(() -> new ModelNotFoundException("Payment not found for Order ID " + orderId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPayments() {
    return paymentAdapterPort.findAllPayments();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPaymentsByMethod(PaymentMethod method) {
    return paymentAdapterPort.findAllPaymentsByMethod(method);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPaymentsByStatus(PaymentStatus status) {
    return paymentAdapterPort.findAllPaymentsByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPaymentsByMethodAndStatus(PaymentMethod method, PaymentStatus status) {
    return paymentAdapterPort.findAllPaymentsByMethodAndStatus(method, status);
  }

  @Override
  @Transactional
  public Payment confirmPaymentById(UUID id) {
    Payment found = getPaymentById(id);
    if (found.isExpiredByTime()) {
      found.markExpired();
      log.info("Payment {} expired automatically due to time limit", found.getId());
      return paymentAdapterPort.saveUpdatePayment(found);
    }
    if (found.amountMismatch()) {
      found.markFailed();
      if (!found.canRetry()) {
        found.markExpired();
        orderPaymentSyncPort.markOrderCanceled(found.getOrder().getId());
      }
      return paymentAdapterPort.saveUpdatePayment(found);
    }
    found.markCompleted();
    orderPaymentSyncPort.markOrderPaid(found.getOrder().getId());
    return paymentAdapterPort.saveUpdatePayment(found);
  }

  @Override
  @Transactional
  public Payment changePaymentMethodById(UUID id, PaymentMethod method) {
    Payment found = getPaymentById(id);
    found.changeMethod(method);
    return paymentAdapterPort.saveUpdatePayment(found);
  }

  @Override
  @Transactional
  public Payment payAgainById(UUID id) {
    Payment found = getPaymentById(id);
    found.markPending();
    return paymentAdapterPort.saveUpdatePayment(found);
  }

  @Override
  @Transactional
  public boolean existsPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    return paymentAdapterPort.existsPaymentByOrderId(found.getId());
  }

  private Order requireOrderConfirmed(UUID orderId) {
    if (orderId == null) throw new InvalidArgumentException("Order ID in Payment cannot be null");
    Order found = orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> new InvalidStateException("Order ID " + orderId + " not found in payment"));
    ValidateStatusUtils.throwIfNotConfirmed(found.getStatus());
    return found;
  }

  private Order ensureOrderExists(UUID orderId) {
    if (orderId == null) throw new InvalidArgumentException("Order ID in Payment cannot be null");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> new InvalidStateException("Order must be CONFIRMED to create a payment"));
  }
}
