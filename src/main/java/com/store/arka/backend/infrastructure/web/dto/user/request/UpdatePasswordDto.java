package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDto(
    @NotBlank(message = "user_id is required")
    String userId,
    @NotBlank(message = "password is required")
    String password
) {
}
