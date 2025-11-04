package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentUpdater {
  public PaymentEntity merge(PaymentEntity entity, Payment domain) {
    if (entity == null || domain == null) return null;
    if (!entity.getMethod().equals(domain.getMethod()))
      entity.setMethod(domain.getMethod());
    if (!entity.getStatus().equals(domain.getStatus()))
      entity.setStatus(domain.getStatus());
    entity.setProcessedAt(domain.getProcessedAt());
    return entity;
  }
}
