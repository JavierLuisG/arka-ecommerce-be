package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserUpdater {
  public UserEntity merge(UserEntity entity, User domain) {
    if (entity == null || domain == null) return null;
    if (!entity.getUserName().equals(domain.getUserName()))
      entity.setUserName(domain.getUserName());
    if (!entity.getEmail().equals(domain.getEmail()))
      entity.setEmail(domain.getEmail());
    if (!entity.getPassword().equals(domain.getPassword()))
      entity.setPassword(domain.getPassword());
    if (!entity.getRole().equals(domain.getRole()))
      entity.setRole(domain.getRole());
    if (!entity.getStatus().equals(domain.getStatus()))
      entity.setStatus(domain.getStatus());
    return entity;
  }
}
