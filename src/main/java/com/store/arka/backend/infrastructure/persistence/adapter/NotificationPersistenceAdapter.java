package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.INotificationAdapterPort;
import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.domain.model.Notification;
import com.store.arka.backend.infrastructure.persistence.mapper.NotificationMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements INotificationAdapterPort {
  private final IJpaNotificationRepository jpaNotificationRepository;
  private final NotificationMapper mapper;

  @Override
  public Notification saveNotification(Notification notification) {
    return mapper.toDomain(jpaNotificationRepository.save(mapper.toEntity(notification)));
  }

  @Override
  public Optional<Notification> findNotificationById(UUID id) {
    return jpaNotificationRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Notification> findNotificationByIdAndStatus(UUID id, NotificationStatus status) {
    return jpaNotificationRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public List<Notification> findAllNotificationsByOrderId(UUID orderId) {
    return jpaNotificationRepository.findAllByOrderId(orderId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Notification> findAllNotificationsByCustomerId(UUID customerId) {
    return jpaNotificationRepository.findAllByCustomerId(customerId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Notification> findAllNotificationsByType(NotificationType type) {
    return jpaNotificationRepository.findAllByType(type).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Notification> findAllNotificationsByStatus(NotificationStatus status) {
    return jpaNotificationRepository.findAllByStatus(status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Notification> findAllNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status) {
    return jpaNotificationRepository.findAllByTypeAndStatus(type, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsNotificationByOrderIdAndType(UUID orderId, NotificationType type) {
    return jpaNotificationRepository.existsByOrderIdAndType(orderId, type);
  }
}
