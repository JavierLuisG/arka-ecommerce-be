package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.out.IPaymentAdapterPort;
import com.store.arka.backend.domain.exception.InvalidStateException;
import com.store.arka.backend.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailerService {
  private final IPaymentAdapterPort paymentAdapterPort;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Payment markFailPaymentById(Payment payment) {
    payment.markFailed();
    if (payment.canRetry()) {
      log.info("Payment {} marked as FAILED", payment.getId());
    } else {
      payment.markExpired();
      log.info("Due to failed attempts, Payment {} has been marked as EXPIRED", payment.getId());
    }
    return paymentAdapterPort.saveUpdatePayment(payment);
  }
}
