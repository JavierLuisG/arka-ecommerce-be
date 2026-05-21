package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IUserUseCase;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdateEmailDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdatePasswordDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdateRoleDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdateUserNameDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.UserResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.UserDtoMapper;
import com.store.arka.backend.shared.util.NormalizationUtils;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final IUserUseCase userUseCase;
  private final UserDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByIdSecure(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER')")
  @GetMapping("/username/{userName}")
  public ResponseEntity<UserResponseDto> getUserByUserName(@PathVariable("userName") String userName) {
    String normalizeUserName = NormalizationUtils.normalizeShortText(userName);
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByUserName(normalizeUserName)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER')")
  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable("email") String email) {
    String normalizeEmail = NormalizationUtils.normalizeShortText(email);
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByEmail(normalizeEmail)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<UserResponseDto>> getAllUsersByFilter(
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String status) {
    UserRole roleEnum = null;
    UserStatus statusEnum = null;
    if (role != null) roleEnum = PathUtils.validateEnumOrThrow(UserRole.class, role, "UserRole");
    if (status != null) statusEnum = PathUtils.validateEnumOrThrow(UserStatus.class, status, "UserStatus");
    return ResponseEntity.ok(userUseCase.getAllUsersByFilters(roleEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/staff-account-role")
  public ResponseEntity<UserResponseDto> updateStaffAccountRole(@RequestBody @Valid UpdateRoleDto dto) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateStaffAccountRole(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/username")
  public ResponseEntity<UserResponseDto> updateUserName(@RequestBody @Valid UpdateUserNameDto dto) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateUserName(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/email")
  public ResponseEntity<UserResponseDto> updateEmail(@RequestBody @Valid UpdateEmailDto dto) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateEmail(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/password")
  public ResponseEntity<UserResponseDto> updatePassword(@RequestBody @Valid UpdatePasswordDto dto) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.updatePassword(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteUser(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    userUseCase.softDeleteUser(uuid);
    return ResponseEntity.ok(new MessageResponseDto("User has been successfully deleted with ID " + uuid));
  }

  @PreAuthorize("hasAnyRole('ADMIN')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<UserResponseDto> restoreUser(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.restoreUser(uuid)));
  }
}
