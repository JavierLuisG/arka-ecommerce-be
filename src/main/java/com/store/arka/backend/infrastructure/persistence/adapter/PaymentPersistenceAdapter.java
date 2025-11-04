package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IPaymentAdapterPort;
import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Payment;
import com.store.arka.backend.infrastructure.persistence.entity.PaymentEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.PaymentMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaPaymentRepository;
import com.store.arka.backend.infrastructure.persistence.updater.PaymentUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements IPaymentAdapterPort {
  private final IJpaPaymentRepository jpaPaymentRepository;
  private final PaymentMapper mapper;
  private final PaymentUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Payment saveCreatePayment(Payment payment) {
    PaymentEntity entity = mapper.toEntity(payment);
    PaymentEntity saved = jpaPaymentRepository.save(entity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Payment saveUpdatePayment(Payment payment) {
    PaymentEntity found = jpaPaymentRepository.findById(payment.getId())
        .orElseThrow(() -> new ModelNotFoundException("Payment with id " + payment.getId() + " not found"));
    PaymentEntity updated = updater.merge(found, payment);
    PaymentEntity saved = jpaPaymentRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Payment> findPaymentById(UUID id) {
    return jpaPaymentRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Payment> findPaymentByOrderId(UUID orderId) {
    return jpaPaymentRepository.findByOrderId(orderId).map(mapper::toDomain);
  }

  @Override
  public List<Payment> findAllPayments() {
    return jpaPaymentRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Payment> findAllPaymentsByMethod(PaymentMethod method) {
    return jpaPaymentRepository.findAllByMethod(method).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Payment> findAllPaymentsByStatus(PaymentStatus status) {
    return jpaPaymentRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Payment> findAllPaymentsByMethodAndStatus(PaymentMethod method, PaymentStatus status) {
    return jpaPaymentRepository.findAllByMethodAndStatus(method, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsPaymentByOrderId(UUID orderId) {
    return jpaPaymentRepository.existsByOrderId(orderId);
  }
}
