package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.InvalidStateException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
  @EqualsAndHashCode.Include
  private final UUID id;
  private String userName;
  private String email;
  private String password;
  private UserRole role;
  private UserStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static User create(String userName, String email, String password) {
    validateNotNullOrEmpty(userName, "UserName");
    validateNotNullOrEmpty(email, "Email");
    validateNotNullOrEmpty(password, "Password");
    return new User(
        null,
        userName,
        email,
        password,
        UserRole.CUSTOMER,
        UserStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateUserName(String userName) {
    validateNotNullOrEmpty(userName, "UserName");
    throwIfDisabled();
    this.userName = userName;
  }

  public void updateEmail(String email) {
    validateNotNullOrEmpty(email, "Email");
    throwIfDisabled();
    this.email = email;
  }

  public void updatePassword(String password) {
    validateNotNullOrEmpty(password, "Password");
    throwIfDisabled();
    this.password = password;
  }

  public void delete() {
    if (!isActive()) throw new InvalidStateException("User must be ACTIVE to be disabled");
    this.status = UserStatus.DISABLED;
  }

  public void restore() {
    if (!isDisabled()) throw new InvalidStateException("User must be DISABLED to be restored");
    this.status = UserStatus.ACTIVE;
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) throw new InvalidArgumentException(field + " must not be null or empty");
  }

  private void throwIfDisabled() {
    if (isDisabled()) throw new InvalidStateException("Cannot be modified by disabled user");
  }

  public boolean isDisabled() {
    return this.status == UserStatus.DISABLED;
  }

  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }
}
