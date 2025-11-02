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

  @GetMapping("/{id}")
  public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(notificationUseCase.getNotificationById(uuid)));
  }

  @GetMapping("/{id}/status/{status}")
  public ResponseEntity<NotificationResponseDto> getNotificationByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    NotificationStatus statusEnum = PathUtils.validateEnumOrThrow(NotificationStatus.class, status, "NotificationStatus");
    return ResponseEntity.ok(mapper.toDto(notificationUseCase.getNotificationByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<NotificationResponseDto>> getNotificationByOrderId(
      @PathVariable("orderId") String orderId) {
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByOrderId(orderUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<NotificationResponseDto>> getNotificationByCustomerId(
      @PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<NotificationResponseDto>> getNotificationByType(
      @PathVariable("type") String type) {
    NotificationType typeEnum = PathUtils.validateEnumOrThrow(NotificationType.class, type, "NotificationType");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByType(typeEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<NotificationResponseDto>> getNotificationByStatus(
      @PathVariable("status") String status) {
    NotificationStatus statusEnum = PathUtils.validateEnumOrThrow(NotificationStatus.class, status, "NotificationStatus");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/type/{type}/status/{status}")
  public ResponseEntity<List<NotificationResponseDto>> getNotificationByTypeAndStatus(
      @PathVariable("type") String type,
      @PathVariable("status") String status) {
    NotificationType typeEnum = PathUtils.validateEnumOrThrow(NotificationType.class, type, "NotificationType");
    NotificationStatus statusEnum = PathUtils.validateEnumOrThrow(NotificationStatus.class, status, "NotificationStatus");
    return ResponseEntity.ok(notificationUseCase.getAllNotificationsByTypeAndStatus(typeEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}/mark-read")
  public ResponseEntity<MessageResponseDto> markNotificationAsReadById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    notificationUseCase.markNotificationAsReadById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Notification has been successfully marked as read"));
  }

  @GetMapping("/type/{type}/order/{orderId}/exists")
  public ResponseEntity<Boolean> checkAvailabilityById(
      @PathVariable("type") String type,
      @PathVariable("orderId") String orderId) {
    NotificationType typeEnum = PathUtils.validateEnumOrThrow(NotificationType.class, type, "NotificationType");
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(notificationUseCase.existsNotificationByOrderIdAndType(orderUuid, typeEnum));
  }
}