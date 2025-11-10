package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.INotificationUseCase;
import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.notification.response.NotificationResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.NotificationDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
  private final INotificationUseCase notificationUseCase;
  private final NotificationDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(notificationUseCase.getNotificationById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(notificationUseCase.getAllNotifications()
          .stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    NotificationStatus statusEnum = PathUtils.validateEnumOrThrow(NotificationStatus.class, status, "NotificationStatus");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<NotificationResponseDto>> getAllNotificationsByOrderId(
      @PathVariable("orderId") String orderId) {
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByOrderId(orderUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<NotificationResponseDto>> getAllNotificationsByCustomerId(
      @PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/type/{type}")
  public ResponseEntity<List<NotificationResponseDto>> getAllNotificationsByType(
      @PathVariable("type") String type) {
    NotificationType typeEnum = PathUtils.validateEnumOrThrow(NotificationType.class, type, "NotificationType");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByType(typeEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/type/{type}/status/{status}")
  public ResponseEntity<List<NotificationResponseDto>> getAllNotificationsByTypeAndStatus(
      @PathVariable("type") String type,
      @PathVariable("status") String status) {
    NotificationType typeEnum = PathUtils.validateEnumOrThrow(NotificationType.class, type, "NotificationType");
    NotificationStatus statusEnum = PathUtils.validateEnumOrThrow(NotificationStatus.class, status, "NotificationStatus");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByTypeAndStatus(typeEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/mark-read")
  public ResponseEntity<MessageResponseDto> markNotificationAsRead(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    notificationUseCase.markNotificationAsRead(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Notification has been successfully marked as read"));
  }
}