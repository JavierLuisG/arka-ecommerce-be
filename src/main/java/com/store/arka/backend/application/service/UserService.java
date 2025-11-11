package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IUserUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.shared.security.SecurityUtils;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserUseCase {
  private final IUserAdapterPort userAdapterPort;
  private final PasswordEncoder passwordEncoder;
  private final SecurityUtils securityUtils;

  @Override
  @Transactional(readOnly = true)
  public User getUserById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "User ID");
    return userAdapterPort.findUserById(id)
        .orElseThrow(() -> {
          log.warn("[USER_SERVICE][GET_BY_ID] User(id={}) not found", id);
          return new ModelNotFoundException("User ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByIdSecure(UUID id) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByUserName(String userName) {
    String normalizedUserName = ValidateAttributesUtils.throwIfValueNotAllowed(userName, "UserName");
    User found = userAdapterPort.findUserByUserName(normalizedUserName)
        .orElseThrow(() -> {
          log.warn("[USER_SERVICE][GET_BY_USERNAME] User(username=({}) not found", normalizedUserName);
          return new ModelNotFoundException("User with user_name " + normalizedUserName + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email");
    User found = userAdapterPort.findUserByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("[USER_SERVICE][GET_BY_EMAIL] User(email={}) not found", normalizedEmail);
          return new ModelNotFoundException("User with email " + normalizedEmail + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersByFilters(String role, String status) {
    if (role != null && status != null) {
      UserRole roleEnum = PathUtils.validateEnumOrThrow(UserRole.class, role, "UserRole");
      UserStatus statusEnum = PathUtils.validateEnumOrThrow(UserStatus.class, status, "UserStatus");
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with role=({}) and status=({})", roleEnum, statusEnum);
      return userAdapterPort.findAllUsersByRoleAndStatus(roleEnum, statusEnum);
    }
    if (role != null) {
      UserRole roleEnum = PathUtils.validateEnumOrThrow(UserRole.class, role, "UserRole");
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with role=({})", roleEnum);
      return userAdapterPort.findAllUsersByRole(roleEnum);
    }
    if (status != null) {
      UserStatus statusEnum = PathUtils.validateEnumOrThrow(UserStatus.class, status, "UserStatus");
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with status=({})", statusEnum);
      return userAdapterPort.findAllUsersByStatus(statusEnum);
    }
    log.info("[USER_SERVICE][GET_ALL] Fetching all Users");
    return userAdapterPort.findAllUsers();
  }

  @Override
  @Transactional
  public User updateUserName(UUID id, String userName) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    String normalizedUserName = ValidateAttributesUtils.throwIfValueNotAllowed(userName, "UserName");
    if (userAdapterPort.existUserByUserName(normalizedUserName) && !found.getUserName().equals(normalizedUserName)) {
      log.warn("[USER_SERVICE][UPDATED_USERNAME] Username={} already exists in users", normalizedUserName);
      throw new FieldAlreadyExistsException("Username " + normalizedUserName + " is already taken");
    }
    found.updateUserName(normalizedUserName);
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_USERNAME] User(id={}) has updated username={} in User(id={})",
        securityUtils.getCurrentUserId(), saved.getUserName(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public User updateEmail(UUID id, String email) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email in User");
    if (userAdapterPort.existUserByEmail(normalizedEmail) && !found.getEmail().equals(normalizedEmail)) {
      log.warn("[USER_SERVICE][UPDATED_EMAIL] Email={} already exists for register in Users", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " is already taken");
    }
    found.updateEmail(normalizedEmail);
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_EMAIL] User(id={}) has updated email={} in User(id={})",
        securityUtils.getCurrentUserId(), saved.getEmail(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public User updatePassword(UUID id, String password) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    String normalizedPassword = ValidateAttributesUtils.throwIfNullOrEmpty(password, "Password");
    String encodedPassword = passwordEncoder.encode(normalizedPassword);
    found.updatePassword(encodedPassword);
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_PASSWORD] User(id={}) has updated password in User(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void softDeleteUser(UUID id) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    found.delete();
    userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][DELETED] User(id={}) has marked as {} the User(id={})",
        securityUtils.getCurrentUserId(), UserStatus.DISABLED, id);
  }

  @Override
  @Transactional
  public User restoreUser(UUID id) {
    User found = getUserById(id);
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    found.restore();
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][RESTORE] User(id={}) has restored the User(id={}) successfully",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }
}
