package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IPaymentUseCase;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.application.port.out.IPaymentAdapterPort;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
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
  private final PaymentFailerService paymentFailerService;

  @Override
  @Transactional
  public Payment createPayment(UUID orderId, Payment payment) {
    Order orderFound = requireOrderConfirmed(orderId);
    if (paymentAdapterPort.existsPaymentByOrderId(orderId)) {
      throw new FieldAlreadyExistsException("A payment already exists for order id " + orderId);
    }
    Payment created = Payment.create(orderFound, payment.getMethod());
    return paymentAdapterPort.saveCreatePayment(created);
  }

  @Override
  @Transactional
  public Payment getPaymentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return paymentAdapterPort.findPaymentById(id)
        .orElseThrow(() -> new ModelNotFoundException("Payment not found for id " + id));
  }

  @Override
  @Transactional
  public Payment getPaymentByOrderId(UUID orderId) {
    Order found = ensureOrderExists(orderId);
    return paymentAdapterPort.findPaymentByOrderId(found.getId())
        .orElseThrow(() -> new ModelNotFoundException("Payment not found for orderId " + orderId));
  }

  @Override
  @Transactional
  public List<Payment> getAllPayments() {
    return paymentAdapterPort.findAllPayments();
  }

  @Override
  @Transactional
  public List<Payment> getAllPaymentsByMethod(PaymentMethod method) {
    return paymentAdapterPort.findAllPaymentsByMethod(method);
  }

  @Override
  @Transactional
  public List<Payment> getAllPaymentsByStatus(PaymentStatus status) {
    return paymentAdapterPort.findAllPaymentsByStatus(status);
  }

  @Override
  @Transactional
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
      }
      return paymentAdapterPort.saveUpdatePayment(found);
    }
    found.markCompleted();
    return paymentAdapterPort.saveUpdatePayment(found);
  }

//  @Override
//  @Transactional
//  public Payment confirmPaymentById(UUID id) {
//    Payment found = getPaymentById(id);
//    if (found.isExpiredByTime()) {
//      found.markExpired();
//      log.info("Payment {} expired automatically due to time limit", found.getId());
//      return paymentAdapterPort.saveUpdatePayment(found);
//    }
//    try {
//      found.validateAmountOrThrow();
//      found.markCompleted();
//      return paymentAdapterPort.saveUpdatePayment(found);
//    } catch (PaymentValidationException | InvalidStateException ex) {
//      paymentFailerService.markFailPaymentById(found);
//      throw ex;
//    }
//  }

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
    if (orderId == null) throw new InvalidArgumentException("OrderId in Payment cannot be null");
    return orderAdapterPort.findOrderByIdAndStatus(orderId, OrderStatus.CONFIRMED)
        .orElseThrow(() -> new InvalidStateException("Cannot create payment: Order "
            + orderId + " must be in CONFIRMED state"));
  }

  private Order ensureOrderExists(UUID orderId) {
    if (orderId == null) throw new InvalidArgumentException("OrderId in Payment cannot be null");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> new InvalidStateException("Order must be CONFIRMED to create a payment"));
  }
}
