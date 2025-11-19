package com.store.arka.backend.application.model;

import com.store.arka.backend.domain.model.User;

public record AuthResult(
    User user,
    String token
) {
}
