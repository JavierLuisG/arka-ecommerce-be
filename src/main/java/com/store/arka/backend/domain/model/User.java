package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.exception.InvalidStateException;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
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
    String normalizedUserName = ValidateAttributesUtils.throwIfValueNotAllowed(userName, "UserName");
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email");
    String normalizedPassword = ValidateAttributesUtils.throwIfNullOrEmpty(password, "Password");
    return new User(
        null,
        normalizedUserName,
        normalizedEmail,
        normalizedPassword,
        UserRole.CUSTOMER,
        UserStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateUserName(String userName) {
    ValidateAttributesUtils.throwIfValueNotAllowed(userName, "UserName");
    throwIfDisabled();
    this.userName = userName;
  }

  public void updateEmail(String email) {
    ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email in User");
    throwIfDisabled();
    this.email = email;
  }

  public void updatePassword(String password) {
    ValidateAttributesUtils.throwIfNullOrEmpty(password, "Password");
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

  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
  }

  public boolean isDisabled() {
    return this.status == UserStatus.DISABLED;
  }

  private void throwIfDisabled() {
    if (isDisabled()) throw new InvalidStateException("Cannot be modified by disabled user");
  }
}
