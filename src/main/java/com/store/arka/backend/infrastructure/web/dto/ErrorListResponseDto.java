package com.store.arka.backend.infrastructure.web.dto;

import java.util.Map;

public record ErrorListResponseDto(
    Integer code,
    Map<String, String> message,
    String path
) {
}
