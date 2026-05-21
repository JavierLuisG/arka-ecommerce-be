package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserUseCase {
  User getUserById(UUID id);

  User getUserByIdSecure(UUID id);

  User getUserByUserName(String userName);

  User getUserByEmail(String email);

  List<User> getAllUsersByFilters(UserRole role, UserStatus status);

  User updateStaffAccountRole(User user);

  User updateUserName(User user);

  User updateEmail(User user);

  User updatePassword(User user);

  void softDeleteUser(UUID id);

  User restoreUser(UUID id);
}
