package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.application.model.AuthResult;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.infrastructure.web.dto.user.request.*;
import com.store.arka.backend.infrastructure.web.dto.user.response.AuthResponseDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.UserResponseDto;
import com.store.arka.backend.shared.util.NormalizationUtils;
import com.store.arka.backend.shared.util.PathUtils;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
  public User toDomain(RegisterCustomerDto dto) {
    if (dto == null) return null;
    return new User(
        null,
        NormalizationUtils.normalizeShortText(dto.userName()),
        NormalizationUtils.normalizeEmail(dto.email()),
        dto.password(),
        null,
        null,
        null,
        null
    );
  }

  public User toDomain(RegisterUserWithRoleDto dto) {
    if (dto == null) return null;
    return new User(
        null,
        NormalizationUtils.normalizeShortText(dto.userName()),
        NormalizationUtils.normalizeEmail(dto.email()),
        dto.password(),
        PathUtils.validateEnumOrThrow(UserRole.class, dto.role(), "UserRole"),
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
        NormalizationUtils.normalizeEmail(dto.email()),
        dto.password(),
        null,
        null,
        null,
        null
    );
  }

  public User toDomain(UpdateRoleDto dto) {
    if (dto == null) return null;
    return new User(
        PathUtils.validateAndParseUUID(dto.userId()),
        null,
        null,
        null,
        PathUtils.validateEnumOrThrow(UserRole.class, dto.role(), "UserRole"),
        null,
        null,
        null
    );
  }

  public User toDomain(UpdateUserNameDto dto) {
    if (dto == null) return null;
    return new User(
        PathUtils.validateAndParseUUID(dto.userId()),
        NormalizationUtils.normalizeShortText(dto.userName()),
        null,
        null,
        null,
        null,
        null,
        null
    );
  }

  public User toDomain(UpdateEmailDto dto) {
    if (dto == null) return null;
    return new User(
        PathUtils.validateAndParseUUID(dto.userId()),
        null,
        NormalizationUtils.normalizeEmail(dto.email()),
        null,
        null,
        null,
        null,
        null
    );
  }

  public User toDomain(UpdatePasswordDto dto) {
    if (dto == null) return null;
    return new User(
        PathUtils.validateAndParseUUID(dto.userId()),
        null,
        null,
        dto.password(),
        null,
        null,
        null,
        null
    );
  }

  public AuthResponseDto toDto(AuthResult result) {
    if (result == null) return null;
    return new AuthResponseDto(
        result.user().getId(),
        result.token()
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
