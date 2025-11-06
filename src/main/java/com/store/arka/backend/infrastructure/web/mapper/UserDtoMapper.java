package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.infrastructure.web.dto.user.request.LoginDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.RegisterDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.AuthResponseDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
  public User toDomain(RegisterDto dto) {
    if (dto == null) return null;
    return new User(
        null,
        dto.userName(),
        dto.email(),
        dto.password(),
        null,
        null,
        null,
        null
    );
  }

  public User toDomain(LoginDto dto) {
    if (dto == null) return null;
    return new User(
        null,
        null,
        dto.email(),
        dto.password(),
        null,
        null,
        null,
        null
    );
  }

  public AuthResponseDto toDto(String jwt) {
    if (jwt == null) return null;
    return new AuthResponseDto(
        jwt
    );
  }

  public UserResponseDto toDto(User user) {
    if (user == null) return null;
    return new UserResponseDto(
        user.getId(),
        user.getUserName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        user.getCreatedAt(),
        user.getUpdatedAt()
    );
  }
}
