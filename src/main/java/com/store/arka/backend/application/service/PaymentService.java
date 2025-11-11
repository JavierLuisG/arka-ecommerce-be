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
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Payment createPayment(UUID orderId, Payment payment) {
    Order orderFound = requireOrderConfirmed(orderId);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    ValidateAttributesUtils.throwIfModelNull(payment, "Payment");
    validateOrderIdExistence(orderId);
    Payment created = Payment.create(orderFound, payment.getMethod());
    Payment saved = paymentAdapterPort.saveCreatePayment(created);
    log.info("[PAYMENT_SERVICE][CREATED] User(id={}) has created new Payment(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Payment ID");
    return paymentAdapterPort.findPaymentById(id)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][GET_BY_ID] Payment(id={}) not found", id);
          return new ModelNotFoundException("Payment not found for ID " + id);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentByIdSecure(UUID id) {
    Payment found = getPaymentById(id);
    securityUtils.requireOwnerOrRoles(found.getOrder().getCustomer().getUserId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public Payment getPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    return paymentAdapterPort.findPaymentByOrderId(found.getId())
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][GET_BY_ORDER] Payment not found by Order(id={})", orderId);
          return new ModelNotFoundException("Payment not found for Order ID " + orderId);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Payment> getAllPaymentsByFilters(String method, String status) {
    if (method != null && status != null) {
      PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
      PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all Payments with method=({}) and status=({})",
          method, status);
      return paymentAdapterPort.findAllPaymentsByMethodAndStatus(methodEnum, statusEnum);
    }
    if (method != null) {
      PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all Payments with method=({})", method);
      return paymentAdapterPort.findAllPaymentsByMethod(methodEnum);
    }
    if (status != null) {
      PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
      log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all Payments with status=({})", status);
      return paymentAdapterPort.findAllPaymentsByStatus(statusEnum);
    }
    log.info("[PAYMENT_SERVICE][GET_ALL] Fetching all Payments");
    return paymentAdapterPort.findAllPayments();
  }

  @Override
  @Transactional
  public Payment confirmPayment(UUID id) {
    Payment found = getPaymentById(id);
    securityUtils.requireOwnerOrRoles(found.getOrder().getCustomer().getUserId(), "ADMIN");
    if (found.isExpiredByTime()) {
      found.markExpired();
      log.info("[PAYMENT_SERVICE][CONFIRMED] Payment(id={}) expired automatically due to time limit", found.getId());
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
    log.info("[PAYMENT_SERVICE][CONFIRMED] User(id={}) has marked the Payment(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), saved.getId(), saved.getStatus());
    return saved;
  }

  @Override
  @Transactional
  public Payment changePaymentMethod(UUID id, PaymentMethod method) {
    Payment found = getPaymentById(id);
    securityUtils.requireOwnerOrRoles(found.getOrder().getCustomer().getUserId(), "ADMIN");
    found.changeMethod(method);
    Payment saved = paymentAdapterPort.saveUpdatePayment(found);
    log.info("[PAYMENT_SERVICE][CHANGE_METHOD] User(id={}) has updated method=({}) in Payment(id={})",
        securityUtils.getCurrentUserId(), saved.getMethod(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Payment payAgain(UUID id) {
    Payment found = getPaymentById(id);
    securityUtils.requireOwnerOrRoles(found.getOrder().getCustomer().getUserId(), "ADMIN");
    found.markPending();
    Payment saved = paymentAdapterPort.saveUpdatePayment(found);
    log.info("[PAYMENT_SERVICE][PAY_AGAIN] User(id={}) has marked the Payment(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), saved.getId(), saved.getMethod());
    return saved;
  }

  private Order requireOrderConfirmed(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in Payment");
    Order found = orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][REQUIRED_ORDER_CONFIRMED] Order(id={}) not found in Payment", orderId);
          return new InvalidStateException("Order ID " + orderId + " not found in payment");
        });
    ValidateStatusUtils.throwIfNotConfirmed(found.getStatus());
    return found;
  }

  private Order ensureOrderExists(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in Payment");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> {
          log.warn("[PAYMENT_SERVICE][ENSURE_ORDER_EXISTS] Order(id={}) not found in Payment", orderId);
          return new InvalidStateException("Order must be CONFIRMED to create a payment");
        });
  }

  private void validateOrderIdExistence(UUID orderId) {
    if (paymentAdapterPort.existsPaymentByOrderId(orderId)) {
      log.warn("[PAYMENT_SERVICE][CREATED] Payment already exists whit this Order(id={})", orderId);
      throw new FieldAlreadyExistsException("A payment already exists for Order ID " + orderId);
    }
  }
}
