package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.shared.util.MessageNotificationType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final Customer customer;
  private final Order order;
  private NotificationType type;
  private String message;
  private NotificationStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime readAt;

  public static Notification create(Customer customer, Order order, NotificationType type) {
    if (customer == null) throw new IllegalArgumentException("Customer in Notification cannot be null");
    if (order == null) throw new IllegalArgumentException("Order in Notification cannot be null");
    String fullName = customer.getFirstName() + " " + customer.getLastName();
    return new Notification(
        null,
        customer,
        order,
        type,
        MessageNotificationType.defaultMessageFor(type, fullName, order.getId()),
        NotificationStatus.UNREAD,
        null,
        null
    );
  }

  public void markAsRead() {
    if (this.status == NotificationStatus.READ) return;
    this.status = NotificationStatus.READ;
    this.readAt = LocalDateTime.now();
  }
}
