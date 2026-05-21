package com.store.arka.backend.infrastructure.web.dto.user.response;

import java.util.UUID;

public record AuthResponseDto(
    UUID userId,
    String jwt
) {
}
