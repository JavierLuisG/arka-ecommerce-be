package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IOrderUseCase;
import com.store.arka.backend.application.port.out.IOrderPaymentSyncPort;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentSyncService implements IOrderPaymentSyncPort {
  private final IOrderUseCase orderUseCase;

  @Override
  public void markOrderPaid(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID");
    orderUseCase.payOrderById(orderId);
    log.info("[ORDER_PAYMENT_SYNC_SERVICE][MARK_ORDER_PAID] Order ID {} was marked as paid", orderId);
  }

  @Override
  public void markOrderCanceled(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID");
    orderUseCase.cancelOrderById(orderId);
    log.info("[ORDER_PAYMENT_SYNC_SERVICE][MARK_ORDER_CANCELED] Order ID {} was marked as cancelled", orderId);
  }
}
