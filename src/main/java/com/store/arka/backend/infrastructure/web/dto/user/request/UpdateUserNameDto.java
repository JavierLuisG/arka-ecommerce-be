package com.store.arka.backend.infrastructure.web.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserNameDto(
    @NotBlank(message = "User_name is required")
    String userName
) {
}
