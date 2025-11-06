package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserUseCase {
  User getUserById(UUID id);

  User getUserByUserName(String userName);

  User getUserByEmail(String email);

  List<User> getAllUsers();

  List<User> getAllUsersByRole(UserRole role);

  List<User> getAllUsersByStatus(UserStatus status);

  List<User> getAllUsersByRoleAndStatus(UserRole role, UserStatus status);

  User updateUserNameById(UUID id, String userName);

  User updateEmailById(UUID id, String email);

  User updatePasswordById(UUID id, String password);

  void softDeleteUserById(UUID id);

  User restoreUserByEmail(String email);

  boolean existUserByUserName(String userName);

  boolean existUserByEmail(String email);
}
