package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.infrastructure.persistence.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {
  private final OrderMapper orderMapper;

  public Payment toDomain(PaymentEntity entity) {
    if (entity == null) return null;
    return new Payment(
        entity.getId(),
        orderMapper.toDomain(entity.getOrder()),
        entity.getAmount(),
        entity.getMethod(),
        entity.getStatus(),
        entity.getFailedAttempts(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getProcessedAt()
    );
  }

  public PaymentEntity toEntity(Payment domain) {
    if (domain == null) return null;
    return new PaymentEntity(
        domain.getId(),
        null,
        orderMapper.toReference(domain.getOrder()),
        domain.getAmount(),
        domain.getMethod(),
        domain.getStatus(),
        domain.getFailedAttempts(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        domain.getProcessedAt()
    );
  }
}
