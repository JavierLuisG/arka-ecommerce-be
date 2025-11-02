package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {
  Optional<NotificationEntity> findByIdAndStatus(UUID id, NotificationStatus status);

  List<NotificationEntity> findAllByOrderId(UUID orderId);

  List<NotificationEntity> findAllByCustomerId(UUID customerId);

  List<NotificationEntity> findAllByType(NotificationType type);

  List<NotificationEntity> findAllByStatus(NotificationStatus status);

  List<NotificationEntity> findAllByTypeAndStatus(NotificationType type, NotificationStatus status);

  boolean existsByOrderIdAndType(UUID orderId, NotificationType type);
}
