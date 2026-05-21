package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserNameDto(
    @NotBlank(message = "user_id is required")
    String userId,
    @NotBlank(message = "user_name is required")
    String userName
) {
}
