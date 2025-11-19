package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterCustomerDto(
    @NotBlank(message = "user_name is required")
    String userName,
    @Email(message = "Email format is invalid")
    @NotBlank(message = "email is required")
    String email,
    @NotBlank(message = "password is required")
    String password
) {
}
