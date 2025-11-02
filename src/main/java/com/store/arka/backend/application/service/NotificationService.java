package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.application.port.in.IEmailService;
import com.store.arka.backend.application.port.in.INotificationUseCase;
import com.store.arka.backend.application.port.out.INotificationAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Notification;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationUseCase {
  private final INotificationAdapterPort notificationAdapterPort;
  private final IEmailService emailService;
  private final IOrderAdapterPort orderAdapterPort;
  private final ICustomerUseCase customerUseCase;

  @Override
  @Transactional
  public Notification createNotification(Notification notification) {
    if (existsNotificationByOrderIdAndType(notification.getOrder().getId(), notification.getType())) {
      throw new FieldAlreadyExistsException(
          notification.getType() + " already exists in order " + notification.getOrder().getId());
    }
    Customer customerFound = findCustomerOrThrow(notification.getCustomer().getId());
    Notification created = Notification.create(customerFound, notification.getOrder(), notification.getType());
    Notification saved = notificationAdapterPort.saveNotification(created);

    emailService.sendNotificationEmail(
        customerFound.getEmail(),
        "Actualización de tu orden #" + notification.getOrder().getId(),
        saved.getMessage()
    );
    return saved;
  }

  @Override
  @Transactional
  public Notification getNotificationById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return notificationAdapterPort.findNotificationById(id)
        .orElseThrow(() -> new ModelNotFoundException("Notification with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Notification getNotificationByIdAndStatus(UUID id, NotificationStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return notificationAdapterPort.findNotificationByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Notification with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Notification> getAllNotificationsByOrderId(UUID orderId) {
    Order order = findOrderOrThrow(orderId);
    return notificationAdapterPort.findAllNotificationsByOrderId(order.getId());
  }

  @Override
  @Transactional
  public List<Notification> getAllNotificationsByCustomerId(UUID customerId) {
    Customer customer = findCustomerOrThrow(customerId);
    return notificationAdapterPort.findAllNotificationsByCustomerId(customer.getId());
  }

  @Override
  @Transactional
  public List<Notification> getAllNotificationsByType(NotificationType type) {
    return notificationAdapterPort.findAllNotificationsByType(type);
  }

  @Override
  @Transactional
  public List<Notification> getAllNotificationsByStatus(NotificationStatus status) {
    return notificationAdapterPort.findAllNotificationsByStatus(status);
  }

  @Override
  @Transactional
  public List<Notification> getAllNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status) {
    return notificationAdapterPort.findAllNotificationsByTypeAndStatus(type, status);
  }

  @Override
  @Transactional
  public void markNotificationAsReadById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    Notification found = getNotificationById(id);
    found.markAsRead();
    notificationAdapterPort.saveNotification(found);
  }

  @Override
  @Transactional
  public boolean existsNotificationByOrderIdAndType(UUID orderId, NotificationType type) {
    Order order = findOrderOrThrow(orderId);
    return notificationAdapterPort.existsNotificationByOrderIdAndType(order.getId(), type);
  }

  private Order findOrderOrThrow(UUID orderId) {
    if (orderId == null) throw new InvalidArgumentException("OrderId in Notification cannot be null");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> new ModelNotFoundException("Order with id " + orderId + " not found"));
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    if (customerId == null) throw new InvalidArgumentException("CustomerId in Notification cannot be null");
    return customerUseCase.getCustomerByIdAndStatus(customerId, CustomerStatus.ACTIVE);
  }
}
