package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleDto(
    @NotBlank(message = "user_id is required")
    String userId,
    @NotBlank(message = "role is required")
    String role
) {
}
