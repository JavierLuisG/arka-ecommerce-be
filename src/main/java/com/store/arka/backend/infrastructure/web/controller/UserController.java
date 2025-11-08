package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IUserUseCase;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdateEmailDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdatePasswordDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.UpdateUserNameDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.UserResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.UserDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserById(uuid)));
  }

  @GetMapping("/username/{userName}")
  public ResponseEntity<UserResponseDto> getUserByUserName(@PathVariable("userName") String userName) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByUserName(userName)));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByEmail(email)));
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> getAllUsers() {
    return ResponseEntity.ok(userUseCase.getAllUsers().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/role/{role}")
  public ResponseEntity<List<UserResponseDto>> getAllUsersByRole(@PathVariable("role") String role) {
    UserRole roleEnum = PathUtils.validateEnumOrThrow(UserRole.class, role, "UserRole");
    return ResponseEntity.ok(userUseCase.getAllUsersByRole(roleEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<UserResponseDto>> getAllUsersByStatus(@PathVariable("status") String status) {
    UserStatus statusEnum = PathUtils.validateEnumOrThrow(UserStatus.class, status, "UserStatus");
    return ResponseEntity.ok(userUseCase.getAllUsersByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/role/{role}/status/{status}")
  public ResponseEntity<List<UserResponseDto>> getAllUsersByRoleAndStatus(
      @PathVariable("role") String role,
      @PathVariable("status") String status) {
    UserRole roleEnum = PathUtils.validateEnumOrThrow(UserRole.class, role, "UserRole");
    UserStatus statusEnum = PathUtils.validateEnumOrThrow(UserStatus.class, status, "UserStatus");
    return ResponseEntity.ok(userUseCase.getAllUsersByRoleAndStatus(roleEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}/update-username")
  public ResponseEntity<UserResponseDto> updateUserNameById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateUserNameDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateUserNameById(uuid, dto.userName())));
  }

  @PutMapping("/{id}/update-email")
  public ResponseEntity<UserResponseDto> updateEmailById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateEmailDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateEmailById(uuid, dto.email())));
  }

  @PutMapping("/{id}/update-password")
  public ResponseEntity<UserResponseDto> updatePasswordById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdatePasswordDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updatePasswordById(uuid, dto.password())));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteUserById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    userUseCase.softDeleteUserById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("User has been successfully deleted with ID " + id));
  }

  @PutMapping("/email/{email}/restore")
  public ResponseEntity<UserResponseDto> restoreProductBySku(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.restoreUserByEmail(email)));
  }

  @GetMapping("/username/{userName}/exists")
  public ResponseEntity<Boolean> existsUserByUserName(@PathVariable("userName") String userName) {
    return ResponseEntity.ok(userUseCase.existUserByUserName(userName));
  }

  @GetMapping("/email/{email}/exists")
  public ResponseEntity<Boolean> existsUserByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(userUseCase.existUserByEmail(email));
  }
}
