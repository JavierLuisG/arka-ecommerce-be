package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IPaymentUseCase;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.application.port.out.IOrderPaymentSyncPort;
import com.store.arka.backend.application.port.out.IPaymentAdapterPort;
import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.shared.util.PathUtils;
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
    ValidateAttributesUtils.throwIfModelNull(payment, "Payment");
    Order orderFound = requireOrderConfirmed(orderId);
    if (paymentAdapterPort.existsPaymentByOrderId(orderId)) {
      log.warn("[PAYMENT_SERVICE][CREATED] Payment already exists whit this order ID {}", orderFound.getId());
      throw new FieldAlreadyExistsException("A payment already exists for Order ID " + orderId);
    }
    Payment created = Payment.create(orderFound, payment.getMethod());
    Payment saved = paymentAdapterPort.saveCreatePayment(created);
    log.info("[PAYMENT_SERVICE][CREATED] Created new payment ID {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Payment ID");
    return paymentAdapterPort.findPaymentById(id)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][GET_BY_ID] Payment ID {} not found", id);
          return new ModelNotFoundException("Payment not found for ID " + id);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    return paymentAdapterPort.findPaymentByOrderId(found.getId())
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][GET_BY_ORDER] Payment not found by order ID {}", orderId);
          return new ModelNotFoundException("Payment not found for Order ID " + orderId);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPaymentsByFilters(String method, String status) {
    if (method != null && status != null) {
      PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
      PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all payments with method {} and status {}",
          method, status);
      return paymentAdapterPort.findAllPaymentsByMethodAndStatus(methodEnum, statusEnum);
    }
    if (method != null) {
      PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all payments with method {}", method);
      return paymentAdapterPort.findAllPaymentsByMethod(methodEnum);
    }
    if (status != null) {
      PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all payments with status {}", status);
      return paymentAdapterPort.findAllPaymentsByStatus(statusEnum);
    }
    log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all payments");
    return paymentAdapterPort.findAllPayments();
  }

  @Override
  @Transactional
  public Payment confirmPaymentById(UUID id) {
    Payment found = getPaymentById(id);
    if (found.isExpiredByTime()) {
      found.markExpired();
      log.info("[PAYMENT_SERVICE][CONFIRMED] Payment ID {} expired automatically due to time limit", found.getId());
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
    Payment saved = paymentAdapterPort.saveUpdatePayment(found);
    log.info("[PAYMENT_SERVICE][CONFIRMED] Payment ID {} was confirmed", id);
    return saved;
  }

  @Override
  @Transactional
  public Payment changePaymentMethodById(UUID id, PaymentMethod method) {
    Payment found = getPaymentById(id);
    found.changeMethod(method);
    Payment saved = paymentAdapterPort.saveUpdatePayment(found);
    log.info("[PAYMENT_SERVICE][CHANGE_METHOD] Updated method {} in payment ID {} ", saved.getMethod(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Payment payAgainById(UUID id) {
    Payment found = getPaymentById(id);
    found.markPending();
    Payment saved = paymentAdapterPort.saveUpdatePayment(found);
    log.info("[PAYMENT_SERVICE][PAY_AGAIN] Payment ID {} was marked pending {} ", saved.getId(), saved.getMethod());
    return saved;
  }

  @Override
  @Transactional
  public boolean existsPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    boolean response = paymentAdapterPort.existsPaymentByOrderId(found.getId());
    log.info("[PAYMENT_SERVICE][EXISTS_BY_ORDER] Response successful {}", response);
    return response;
  }

  private Order requireOrderConfirmed(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in Payment");
    Order found = orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][REQUIRED_ORDER_CONFIRMED] Order ID {} not found in payment", orderId);
          return new InvalidStateException("Order ID " + orderId + " not found in payment");
        });
    ValidateStatusUtils.throwIfNotConfirmed(found.getStatus());
    return found;
  }

  private Order ensureOrderExists(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in Payment");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][ENSURE_ORDER_EXISTS] Order ID {} not found in payment", orderId);
          return new InvalidStateException("Order must be CONFIRMED to create a payment");
        });
  }
}
