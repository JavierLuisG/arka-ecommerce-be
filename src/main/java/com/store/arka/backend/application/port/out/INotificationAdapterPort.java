package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INotificationAdapterPort {
  Notification saveNotification(Notification notification);

  Optional<Notification> findNotificationById(UUID id);

  Optional<Notification> findNotificationByIdAndStatus(UUID id, NotificationStatus status);

  List<Notification> findAllNotificationsByOrderId(UUID orderId);

  List<Notification> findAllNotificationsByCustomerId(UUID customerId);

  List<Notification> findAllNotificationsByType(NotificationType type);

  List<Notification> findAllNotificationsByStatus(NotificationStatus status);

  List<Notification> findAllNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status);

  boolean existsNotificationByOrderIdAndType(UUID orderId, NotificationType type);
}
