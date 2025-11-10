package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {
  @PersistenceContext
  private EntityManager entityManager;

  public User toDomain(UserEntity entity) {
    if (entity == null) return null;
    return new User(
        entity.getId(),
        entity.getUserName(),
        entity.getEmail(),
        entity.getPassword(),
        entity.getRole(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public UserEntity toEntity(User domain) {
    if (domain == null) return null;
    return new UserEntity(
        domain.getId(),
        domain.getUserName(),
        domain.getEmail(),
        domain.getPassword(),
        domain.getRole(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public UserEntity toReference(UUID userId) {
    if (userId == null) return null;
    return entityManager.getReference(UserEntity.class, userId);
  }
}
