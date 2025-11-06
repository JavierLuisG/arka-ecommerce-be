package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailDto(
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    String email
) {
}
