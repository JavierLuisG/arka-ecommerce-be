package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaUserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByUserName(String userName);

  Optional<UserEntity> findByEmail(String email);

  List<UserEntity> findAllByRole(UserRole role);

  List<UserEntity> findAllByStatus(UserStatus status);

  List<UserEntity> findAllUsersByRoleAndStatus(UserRole role, UserStatus status);

  boolean existsByUserName(String userName);

  boolean existsByEmail(String email);
}
