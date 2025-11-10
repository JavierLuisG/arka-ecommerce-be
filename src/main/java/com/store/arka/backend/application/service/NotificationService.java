package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.application.port.in.IEmailService;
import com.store.arka.backend.application.port.in.INotificationUseCase;
import com.store.arka.backend.application.port.out.INotificationAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Notification;
import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.shared.security.SecurityUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationUseCase {
  private final INotificationAdapterPort notificationAdapterPort;
  private final IEmailService emailService;
  private final IOrderAdapterPort orderAdapterPort;
  private final ICustomerUseCase customerUseCase;
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Notification createNotification(Notification notification) {
    validateNotificationUniqueness(notification.getOrder().getId(), notification.getType());
    Customer customerFound = findCustomerOrThrow(notification.getCustomer().getId());
    Notification created = Notification.create(customerFound, notification.getOrder(), notification.getType());
    Notification saved = notificationAdapterPort.saveNotification(created);
    emailService.sendNotificationEmail(
        customerFound.getEmail(),
        "Actualización de tu orden #" + notification.getOrder().getId(),
        saved.getMessage()
    );
    log.info("[NOTIFICATION_SERVICE][CREATED] User(id={}) has created new Notification(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Notification getNotificationById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Notification ID");
    return notificationAdapterPort.findNotificationById(id)
        .orElseThrow(() -> {
          log.warn("[NOTIFICATION_SERVICE][GET_BY_ID] Notification(id={}) not found", id);
          return new ModelNotFoundException("Notification ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotifications() {
    log.info("[NOTIFICATION_SERVICE][GET_ALL] Fetching all Notifications");
    return notificationAdapterPort.findAllNotifications();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotificationsByStatus(NotificationStatus status) {
    log.info("[NOTIFICATION_SERVICE][GET_ALL_BY_STATUS] Fetching all Notifications with status=({})", status);
    return notificationAdapterPort.findAllNotificationsByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotificationsByOrderId(UUID orderId) {
    Order order = findOrderOrThrow(orderId);
    log.info("[NOTIFICATION_SERVICE][GET_ALL_BY_ORDER] Fetching all Notifications with Order(id={})", orderId);
    return notificationAdapterPort.findAllNotificationsByOrderId(order.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotificationsByCustomerId(UUID customerId) {
    Customer customer = findCustomerOrThrow(customerId);
    securityUtils.requireOwnerOrRoles(customer.getUserId(), "ADMIN", "MANAGER");
    log.info("[NOTIFICATION_SERVICE][GET_ALL_BY_CUSTOMER] Fetching all Notifications with Customer(id={})", customerId);
    return notificationAdapterPort.findAllNotificationsByCustomerId(customer.getId());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotificationsByType(NotificationType type) {
    log.info("[NOTIFICATION_SERVICE][GET_ALL_BY_TYPE] Fetching all Notifications with type=({})", type);
    return notificationAdapterPort.findAllNotificationsByType(type);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Notification> getAllNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status) {
    log.info("[NOTIFICATION_SERVICE][GET_ALL_BY_TYPE_AND_STATUS] Fetching all Notifications with type=({}) and status=({})",
        type, status);
    return notificationAdapterPort.findAllNotificationsByTypeAndStatus(type, status);
  }

  @Override
  @Transactional
  public void markNotificationAsRead(UUID id) {
    Notification found = getNotificationById(id);
    securityUtils.requireOwnerOrRoles(found.getOrder().getCustomer().getUserId(), "ADMIN");
    found.markAsRead();
    notificationAdapterPort.saveNotification(found);
    log.info("[NOTIFICATION_SERVICE][AS_READ] User(id={}) has marked as READ Notification(id={})",
        securityUtils.getCurrentUserId(), id);
  }

  private Order findOrderOrThrow(UUID orderId) {
    ValidateAttributesUtils.throwIfIdNull(orderId,"Order ID in Notification");
    return orderAdapterPort.findOrderById(orderId)
        .orElseThrow(() -> {
          log.warn("[NOTIFICATION_SERVICE][FIND_ORDER] Order(id={}) in Notification not found", orderId);
          return new ModelNotFoundException("Order ID " + orderId + " not found");
        });
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(customerId, "Customer ID in Notification");
    Customer customer = customerUseCase.getCustomerById(customerId);
    customer.throwIfDeleted();
    return customer;
  }

  private void validateNotificationUniqueness(UUID orderId, NotificationType type) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in Notification");
    ValidateAttributesUtils.throwIfModelNull(type, "Notification type");
    if (notificationAdapterPort.existsNotificationByOrderIdAndType(orderId, type)) {
      log.warn("[NOTIFICATION_SERVICE][CREATED] Notification(id={}) already exists in Order(id={})", type, orderId);
      throw new FieldAlreadyExistsException("Already exists Notification " + type + " in Order " + orderId);
    }
  }
}
