package com.store.arka.backend.application.port.out;

import java.util.UUID;

public interface IOrderPaymentSyncPort {
  void markOrderPaid(UUID orderId);
  void markOrderCanceled(UUID orderId);
}
