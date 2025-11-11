package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserUseCase {
  User getUserById(UUID id);

  User getUserByIdSecure(UUID id);

  User getUserByUserName(String userName);

  User getUserByEmail(String email);

  List<User> getAllUsersByFilters(String role, String status);

  User updateUserName(UUID id, String userName);

  User updateEmail(UUID id, String email);

  User updatePassword(UUID id, String password);

  void softDeleteUser(UUID id);

  User restoreUser(UUID id);
}
