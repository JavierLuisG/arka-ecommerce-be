package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserWithRoleDto(
    @NotBlank(message = "user_name is required")
    String userName,
    @Email(message = "Email format is invalid")
    @NotBlank(message = "email is required")
    String email,
    @NotBlank(message = "password is required")
    String password,
    @NotBlank(message = "role is required")
    String role
) {
}
