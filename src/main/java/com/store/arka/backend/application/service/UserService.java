package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IUserUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserUseCase {
  private final IUserAdapterPort userAdapterPort;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  public User getUserById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "User ID");
    return userAdapterPort.findUserById(id)
        .orElseThrow(() -> new ModelNotFoundException("User ID " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByUserName(String userName) {
    throwIfUserNameNull(userName);
    String normalizedUserName = userName.trim().toLowerCase();
    return userAdapterPort.findUserByUserName(normalizedUserName)
        .orElseThrow(() -> new ModelNotFoundException("User with user_name " + normalizedUserName + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    throwIfEmailNull(email);
    String normalizedEmail = email.trim().toLowerCase();
    return userAdapterPort.findUserByEmail(normalizedEmail)
        .orElseThrow(() -> new ModelNotFoundException("User with email " + normalizedEmail + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userAdapterPort.findAllUsers();
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersByRole(UserRole role) {
    return userAdapterPort.findAllUsersByRole(role);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersByStatus(UserStatus status) {
    return userAdapterPort.findAllUsersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> getAllUsersByRoleAndStatus(UserRole role, UserStatus status) {
    return userAdapterPort.findAllUsersByRoleAndStatus(role, status);
  }

  @Override
  @Transactional
  public User updateUserNameById(UUID id, String userName) {
    throwIfUserNameNull(userName);
    String normalizedUserName = userName.trim().toLowerCase();
    User found = getUserById(id);
    found.updateUserName(normalizedUserName);
    return userAdapterPort.saveUpdateUser(found);
  }

  @Override
  @Transactional
  public User updateEmailById(UUID id, String email) {
    throwIfUserNameNull(email);
    String normalizedEmail = email.trim().toLowerCase();
    User found = getUserById(id);
    found.updateEmail(normalizedEmail);
    return userAdapterPort.saveUpdateUser(found);
  }

  @Override
  @Transactional
  public User updatePasswordById(UUID id, String password) {
    throwIfPasswordNull(password);
    User found = getUserById(id);
    String encodedPassword = passwordEncoder.encode(password);
    found.updatePassword(encodedPassword);
    return userAdapterPort.saveUpdateUser(found);
  }

  @Override
  @Transactional
  public void softDeleteUserById(UUID id) {
    User found = getUserById(id);
    found.delete();
    userAdapterPort.saveUpdateUser(found);
  }

  @Override
  @Transactional
  public User restoreUserByEmail(String email) {
    throwIfEmailNull(email);
    String normalizedEmail = email.trim().toLowerCase();
    User found = getUserByEmail(normalizedEmail);
    found.restore();
    return userAdapterPort.saveUpdateUser(found);
  }

  @Override
  @Transactional
  public boolean existUserByUserName(String userName) {
    throwIfUserNameNull(userName);
    String normalizedUserName = userName.trim().toLowerCase();
    return userAdapterPort.existUserByUserName(normalizedUserName);
  }

  @Override
  @Transactional
  public boolean existUserByEmail(String email) {
    throwIfEmailNull(email);
    String normalizedEmail = email.trim().toLowerCase();
    return userAdapterPort.existUserByEmail(normalizedEmail);
  }

  private static void throwIfUserNameNull(String userName) {
    if (userName == null) throw new InvalidArgumentException("UserName in User cannot be null");
  }

  private static void throwIfEmailNull(String email) {
    if (email == null) throw new InvalidArgumentException("Email in User cannot be null");
  }

  private static void throwIfPasswordNull(String password) {
    if (password == null) throw new InvalidArgumentException("Password in User cannot be null");
  }
}
