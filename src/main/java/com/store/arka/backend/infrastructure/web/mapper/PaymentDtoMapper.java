package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.infrastructure.web.dto.payment.request.CreatePaymentDto;
import com.store.arka.backend.infrastructure.web.dto.payment.request.UpdatePaymentMethodDto;
import com.store.arka.backend.infrastructure.web.dto.payment.response.PaymentResponseDto;
import com.store.arka.backend.shared.util.PathUtils;
import org.springframework.stereotype.Component;

@Component
public class PaymentDtoMapper {
  public Payment toDomain(CreatePaymentDto dto) {
    if (dto == null) return null;
    return new Payment(
        null,
        null,
        null,
        PathUtils.validateEnumOrThrow(PaymentMethod.class, dto.method(), "PaymentMethod"),
        null,
        null,
        null,
        null,
        null
    );
  }

  public Payment toDomain(UpdatePaymentMethodDto dto) {
    if (dto == null) return null;
    return new Payment(
        null,
        null,
        null,
        PathUtils.validateEnumOrThrow(PaymentMethod.class, dto.method(), "PaymentMethod"),
        null,
        null,
        null,
        null,
        null
    );
  }

  public PaymentResponseDto toDto(Payment domain) {
    if (domain == null) return null;
    return new PaymentResponseDto(
        domain.getId(),
        domain.getOrder().getId(),
        domain.getAmount(),
        domain.getMethod(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        domain.getProcessedAt()
    );
  }
}
