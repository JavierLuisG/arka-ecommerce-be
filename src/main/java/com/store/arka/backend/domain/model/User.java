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

  public static User registerCustomer(String userName, String email, String password) {
    ValidateAttributesUtils.validateValueNotAllowed(userName, "UserName");
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email");
    ValidateAttributesUtils.validateNullOrEmpty(password, "Password");
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

  public static User createWithRole(String userName, String email, String password, UserRole role) {
    if (role == UserRole.CUSTOMER) throw new InvalidStateException("User Customer cannot be created with this endpoint");
    ValidateAttributesUtils.validateValueNotAllowed(userName, "UserName");
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email");
    ValidateAttributesUtils.validateNullOrEmpty(password, "Password");
    ValidateAttributesUtils.validateModel(role, "Role");
    return new User(
        null,
        userName,
        email,
        password,
        role,
        UserStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateStaffAccountRole(UserRole role) {
    ValidateAttributesUtils.validateModel(role, "Role");
    throwIfDisabled();
    this.role = role;
  }

  public void updateUserName(String userName) {
    ValidateAttributesUtils.validateValueNotAllowed(userName, "UserName");
    throwIfDisabled();
    this.userName = userName;
  }

  public void updateEmail(String email) {
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email in User");
    throwIfDisabled();
    this.email = email;
  }

  public void updatePassword(String password) {
    ValidateAttributesUtils.validateNullOrEmpty(password, "Password");
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
