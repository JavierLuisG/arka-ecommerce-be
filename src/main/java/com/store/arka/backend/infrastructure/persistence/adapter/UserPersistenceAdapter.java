package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.infrastructure.persistence.entity.UserEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.UserMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaUserRepository;
import com.store.arka.backend.infrastructure.persistence.updater.UserUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements IUserAdapterPort {
  private final IJpaUserRepository jpaUserRepository;
  private final UserMapper mapper;
  private final UserUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public User saveCreateUser(User user) {
    UserEntity entity = mapper.toEntity(user);
    UserEntity saved = jpaUserRepository.save(entity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public User saveUpdateUser(User user) {
    UserEntity entity = jpaUserRepository.findById(user.getId()).orElseThrow();
    UserEntity updated = updater.merge(entity, user);
    UserEntity saved = jpaUserRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<User> findUserById(UUID id) {
    return jpaUserRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<User> findUserByUserName(String userName) {
    return jpaUserRepository.findByUserName(userName).map(mapper::toDomain);
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return jpaUserRepository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public List<User> findAllUsers() {
    return jpaUserRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<User> findAllUsersByRole(UserRole role) {
    return jpaUserRepository.findAllByRole(role).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<User> findAllUsersByStatus(UserStatus status) {
    return jpaUserRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<User> findAllUsersByRoleAndStatus(UserRole role, UserStatus status) {
    return jpaUserRepository.findAllUsersByRoleAndStatus(role, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsUserById(UUID id) {
    return jpaUserRepository.existsById(id);
  }

  @Override
  public boolean existUserByUserName(String userName) {
    return jpaUserRepository.existsByUserName(userName);
  }

  @Override
  public boolean existUserByEmail(String email) {
    return jpaUserRepository.existsByEmail(email);
  }
}
