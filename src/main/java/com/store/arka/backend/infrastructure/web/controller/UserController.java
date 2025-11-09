package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IUserUseCase;
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
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER')")
  @GetMapping("/username/{userName}")
  public ResponseEntity<UserResponseDto> getUserByUserName(@PathVariable("userName") String userName) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByUserName(userName)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER')")
  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(userUseCase.getUserByEmail(email)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<UserResponseDto>> getAllUsersByFilter(
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String status) {
    return ResponseEntity.ok(userUseCase.getAllUsersByFilters(role, status)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @PutMapping("/{id}/username")
  public ResponseEntity<UserResponseDto> updateUserName(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateUserNameDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateUserName(uuid, dto.userName())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @PutMapping("/{id}/email")
  public ResponseEntity<UserResponseDto> updateEmail(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateEmailDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updateEmail(uuid, dto.email())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @PutMapping("/{id}/password")
  public ResponseEntity<UserResponseDto> updatePassword(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdatePasswordDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.updatePassword(uuid, dto.password())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteUser(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    userUseCase.softDeleteUser(uuid);
    return ResponseEntity.ok(new MessageResponseDto("User has been successfully deleted with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER','CUSTOMER')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<UserResponseDto> restoreUser(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(userUseCase.restoreUser(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/username/{userName}/exists")
  public ResponseEntity<Boolean> existsUserByUserName(@PathVariable("userName") String userName) {
    return ResponseEntity.ok(userUseCase.existUserByUserName(userName));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/email/{email}/exists")
  public ResponseEntity<Boolean> existsUserByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(userUseCase.existUserByEmail(email));
  }
}
