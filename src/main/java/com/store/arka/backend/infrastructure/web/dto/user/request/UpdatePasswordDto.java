package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDto(
    @NotBlank(message = "Password is required")
    String password
) {
}
