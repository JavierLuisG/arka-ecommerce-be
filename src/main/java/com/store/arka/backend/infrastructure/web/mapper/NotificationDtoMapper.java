package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Notification;
import com.store.arka.backend.infrastructure.web.dto.notification.response.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDtoMapper {

  public NotificationResponseDto toDto(Notification domain) {
    return new NotificationResponseDto(
        domain.getId(),
        domain.getOrder().getId(),
        domain.getCustomer().getId(),
        domain.getType(),
        domain.getMessage(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getReadAt()
    );
  }
}
