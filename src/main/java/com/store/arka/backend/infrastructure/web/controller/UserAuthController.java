package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IUserAuthUseCase;
import com.store.arka.backend.infrastructure.web.dto.user.request.LoginDto;
import com.store.arka.backend.infrastructure.web.dto.user.request.RegisterDto;
import com.store.arka.backend.infrastructure.web.dto.user.response.AuthResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterDto request) {
    return ResponseEntity.ok(mapper.toDto(authUserUseCase.register(mapper.toDomain(request))));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto request) {
    return ResponseEntity.ok(mapper.toDto(authUserUseCase.login(mapper.toDomain(request))));
  }
}
