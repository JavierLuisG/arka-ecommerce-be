package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
  Optional<PaymentEntity> findByOrderId(UUID orderId);

  List<PaymentEntity> findAllByMethod(PaymentMethod method);

  List<PaymentEntity> findAllByStatus(PaymentStatus status);

  List<PaymentEntity> findAllByMethodAndStatus(PaymentMethod method, PaymentStatus status);

  boolean existsByOrderId(UUID orderId);
}
