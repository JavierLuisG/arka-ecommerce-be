package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Notification;
import com.store.arka.backend.infrastructure.persistence.entity.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
  private final CustomerMapper customerMapper;
  private final OrderMapper orderMapper;

  public Notification toDomain(NotificationEntity entity) {
    return new Notification(
        entity.getId(),
        customerMapper.toDomain(entity.getCustomer()),
        orderMapper.toDomain(entity.getOrder()),
        entity.getType(),
        entity.getMessage(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getReadAt()
    );
  }

  public NotificationEntity toEntity(Notification domain) {
    return new NotificationEntity(
        domain.getId(),
        customerMapper.toReference(domain.getCustomer()),
        orderMapper.toReference(domain.getOrder()),
        domain.getType(),
        domain.getMessage(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getReadAt()
    );
  }
}
