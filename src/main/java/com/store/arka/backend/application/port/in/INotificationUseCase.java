package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface INotificationUseCase {
  Notification createNotification(Notification notification);

  Notification getNotificationById(UUID id);

  List<Notification> getAllNotifications();

  List<Notification> getAllNotificationsByStatus(NotificationStatus status);

  List<Notification> getAllNotificationsByOrderId(UUID orderId);

  List<Notification> getAllNotificationsByCustomerId(UUID customerId);

  List<Notification> getAllNotificationsByType(NotificationType type);

  List<Notification> getAllNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status);

  void markNotificationAsRead(UUID id);

  boolean existsNotificationByOrderIdAndType(UUID orderId, NotificationType type);
}
