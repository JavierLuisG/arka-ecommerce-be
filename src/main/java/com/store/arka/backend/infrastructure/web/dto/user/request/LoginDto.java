package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(
    @Email(message = "Email format is invalid")
    @NotBlank(message = "email is required")
    String email,
    @NotBlank(message = "password is required")
    String password
) {
}
