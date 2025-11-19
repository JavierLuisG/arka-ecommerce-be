package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IUserAuthUseCase;
import com.store.arka.backend.infrastructure.web.dto.user.request.LoginDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.RegisterCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.RegisterUserWithRoleDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.AuthResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {
  private final IUserAuthUseCase authUserUseCase;
  private final UserDtoMapper mapper;

  @PostMapping("/customer")
  public ResponseEntity<AuthResponseDto> registerCustomer(@RequestBody @Valid RegisterCustomerDto request) {
    return ResponseEntity.ok(mapper.toDto(authUserUseCase.registerCustomer(mapper.toDomain(request))));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/staff-account")
  public ResponseEntity<AuthResponseDto> registerStaffAccount(@RequestBody @Valid RegisterUserWithRoleDto request) {
    return ResponseEntity.ok(mapper.toDto(authUserUseCase.registerStaffAccount(mapper.toDomain(request))));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto request) {
    return ResponseEntity.ok(mapper.toDto(authUserUseCase.login(mapper.toDomain(request))));
  }
}
