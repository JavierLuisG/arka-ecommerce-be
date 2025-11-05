package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IOrderUseCase;
import com.store.arka.backend.application.port.out.IOrderPaymentSyncPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPaymentSyncService implements IOrderPaymentSyncPort {
  private final IOrderUseCase orderUseCase;

  @Override
  public void markOrderPaid(UUID orderId) {
    orderUseCase.payOrderById(orderId);
  }

  @Override
  public void markOrderCanceled(UUID orderId) {
    orderUseCase.cancelOrderById(orderId);
  }
}
