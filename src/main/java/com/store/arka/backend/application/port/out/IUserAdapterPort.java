package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserAdapterPort {
  User saveCreateUser(User user);

  User saveUpdateUser(User user);

  Optional<User> findUserById(UUID id);

  Optional<User> findUserByUserName(String userName);

  Optional<User> findUserByEmail(String email);

  List<User> findAllUsers();

  List<User> findAllUsersByRole(UserRole role);

  List<User> findAllUsersByStatus(UserStatus status);

  List<User> findAllUsersByRoleAndStatus(UserRole role, UserStatus status);

  boolean existsUserById(UUID id);

  boolean existUserByUserName(String userName);

  boolean existUserByEmail(String email);
}
