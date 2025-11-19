package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailDto(
    @NotBlank(message = "user_id is required")
    String userId,
    @Email(message = "Email format is invalid")
    @NotBlank(message = "email is required")
    String email
) {
}
