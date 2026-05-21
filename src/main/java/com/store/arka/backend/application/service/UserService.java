package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IUserUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidStateException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.shared.security.SecurityUtils;
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
    ValidateAttributesUtils.validateId(id, "ID in User");
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
    ValidateAttributesUtils.validateValueNotAllowed(userName, "UserName");
    User found = userAdapterPort.findUserByUserName(userName)
        .orElseThrow(() -> {
          log.warn("[USER_SERVICE][GET_BY_USERNAME] User(username=({}) not found", userName);
          return new ModelNotFoundException("User with user_name " + userName + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email");
    User found = userAdapterPort.findUserByEmail(email)
        .orElseThrow(() -> {
          log.warn("[USER_SERVICE][GET_BY_EMAIL] User(email={}) not found", email);
          return new ModelNotFoundException("User with email " + email + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersByFilters(UserRole role, UserStatus status) {
    if (role != null && status != null) {
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with role=({}) and status=({})", role, status);
      return userAdapterPort.findAllUsersByRoleAndStatus(role, status);
    }
    if (role != null) {
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with role=({})", role);
      return userAdapterPort.findAllUsersByRole(role);
    }
    if (status != null) {
      log.info("[USER_SERVICE][GET_ALL] Fetching all Users with status=({})", status);
      return userAdapterPort.findAllUsersByStatus(status);
    }
    log.info("[USER_SERVICE][GET_ALL] Fetching all Users");
    return userAdapterPort.findAllUsers();
  }

  @Override
  @Transactional
  public User updateStaffAccountRole(User user) {
    User found = getUserById(user.getId());
    if (found.getRole() == UserRole.CUSTOMER) {
      log.warn("[USER_SERVICE][UPDATED_ROLE] User(id={}) whit role={} cannot be updated",
          securityUtils.getCurrentUserId(), found.getRole());
      throw new InvalidStateException("User Customer cannot be updated");
    }
    found.updateStaffAccountRole(user.getRole());
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_ROLE] User(id={}) has updated role={} in User(id={})",
        securityUtils.getCurrentUserId(), saved.getRole(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public User updateUserName(User user) {
    User found = getUserById(user.getId());
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    if (userAdapterPort.existsUserByUserName(user.getUserName()) && !found.getUserName().equals(user.getUserName())) {
      log.warn("[USER_SERVICE][UPDATED_USERNAME] Username={} already exists in users", user.getUserName());
      throw new FieldAlreadyExistsException("Username " + user.getUserName() + " is already taken");
    }
    found.updateUserName(user.getUserName());
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_USERNAME] User(id={}) has updated username={} in User(id={})",
        securityUtils.getCurrentUserId(), saved.getUserName(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public User updateEmail(User user) {
    User found = getUserById(user.getId());
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    if (userAdapterPort.existsUserByEmail(user.getEmail()) && !found.getEmail().equals(user.getEmail())) {
      log.warn("[USER_SERVICE][UPDATED_EMAIL] Email={} already exists for register in Users", user.getEmail());
      throw new FieldAlreadyExistsException("Email " + user.getEmail() + " is already taken");
    }
    found.updateEmail(user.getEmail());
    User saved = userAdapterPort.saveUpdateUser(found);
    log.info("[USER_SERVICE][UPDATED_EMAIL] User(id={}) has updated email={} in User(id={})",
        securityUtils.getCurrentUserId(), saved.getEmail(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public User updatePassword(User user) {
    User found = getUserById(user.getId());
    securityUtils.requireOwnerOrRoles(found.getId(), "ADMIN");
    String encodedPassword = passwordEncoder.encode(user.getPassword());
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
