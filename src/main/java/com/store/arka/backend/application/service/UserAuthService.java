package com.store.arka.backend.application.service;

import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.infrastructure.security.jwt.JwtService;
import com.store.arka.backend.infrastructure.security.UserDetailsImpl;
import com.store.arka.backend.application.port.in.IUserAuthUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService implements IUserAuthUseCase {
  private final IUserAdapterPort userAdapterPort;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Override
  @Transactional
  public String register(User user) {
    ValidateAttributesUtils.throwIfModelNull(user, "User");
    String normalizedUserName = ValidateAttributesUtils.throwIfValueNotAllowed(user.getUserName(), "UserName in register");
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(user.getEmail(), "Email in register");
    if (userAdapterPort.existUserByUserName(normalizedUserName)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Username {} already exists for register a user", normalizedUserName);
      throw new FieldAlreadyExistsException("Username " + user.getUserName() + " is already taken");
    }
    if (userAdapterPort.existUserByEmail(normalizedEmail)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Email {} already exists for register a user", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + user.getEmail() + " is already registered");
    }
    // crear y guardar el usuario con contraseña encriptada
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User created = User.create(normalizedUserName, normalizedEmail, encodedPassword);
    User saved = userAdapterPort.saveCreateUser(created);
    log.info("[USER_AUTH_SERVICE][REGISTER] Created new user ID {}", saved.getId());
    // generar el token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(saved);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][REGISTER] Generated token for user registered ID {}", saved.getId());
    return token;
  }

  @Override
  @Transactional
  public String login(User user) {
    ValidateAttributesUtils.throwIfModelNull(user, "User");
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(user.getEmail(), "Email in register");
    // Autenticar usando email y contraseña
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, user.getPassword()));
    // Obtener el usuario desde base de datos
    User found = userAdapterPort.findUserByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("[USER_AUTH_SERVICE][LOGIN] User with email {} not found", normalizedEmail);
          return new com.store.arka.backend.domain.exception.UserNotFoundException(
              "User with email " + normalizedEmail + " not found");
        });
    // Generar token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(found);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][LOGIN] Generated token for user logged in ID {}", found.getId());
    return token;
  }
}
