package com.store.arka.backend.infrastructure.web.dto.user.response;

import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String userName,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
