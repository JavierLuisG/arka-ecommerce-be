package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IOrderUseCase;
import com.store.arka.backend.application.port.out.IOrderPaymentSyncPort;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  public void markOrderPaid(UUID orderId) {
    ValidateAttributesUtils.validateId(orderId, "Order ID");
    orderUseCase.payOrder(orderId);
    log.info("[ORDER_PAYMENT_SYNC_SERVICE][MARK_ORDER_PAID] In User(id={}) has been marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), orderId, OrderStatus.PAID);
  }

  @Override
  public void markOrderCanceled(UUID orderId) {
    ValidateAttributesUtils.validateId(orderId, "Order ID");
    orderUseCase.cancelOrder(orderId);
    log.info("[ORDER_PAYMENT_SYNC_SERVICE][MARK_ORDER_CANCELED] In User(id={}) has been marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), orderId, OrderStatus.CANCELED);
  }
}
