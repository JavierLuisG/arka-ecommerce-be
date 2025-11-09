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

  List<User> getAllUsersByFilters(UserRole role, UserStatus status);

  User updateUserName(UUID id, String userName);

  User updateEmail(UUID id, String email);

  User updatePassword(UUID id, String password);

  void softDeleteUser(UUID id);

  User restoreUser(UUID id);

  boolean existUserByUserName(String userName);

  boolean existUserByEmail(String email);
}
