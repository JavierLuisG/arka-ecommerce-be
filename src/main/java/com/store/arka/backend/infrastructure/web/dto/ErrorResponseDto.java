package com.store.arka.backend.infrastructure.web.dto;

public record ErrorResponseDto(
    Integer code,
    String message,
    String path
) {
}


