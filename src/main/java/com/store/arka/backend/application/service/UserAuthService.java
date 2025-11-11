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
    validateUserNameExistence(user.getUserName());
    validateEmailExistence(user.getEmail());
    // encriptar contraseña
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User created = User.create(user.getUserName(), user.getEmail(), encodedPassword);
    User saved = userAdapterPort.saveCreateUser(created);
    log.info("[USER_AUTH_SERVICE][REGISTER] Created new User(id={})", saved.getId());
    // generar el token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(saved);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][REGISTER] Generated token for User(id={}) registered", saved.getId());
    return token;
  }

  @Override
  @Transactional
  public String login(User user) {
    ValidateAttributesUtils.throwIfModelNull(user, "User");
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(user.getEmail(), "Email in register");
    // Autenticar usando email y contraseña
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, user.getPassword()));
    User found = getUser(normalizedEmail);
    // Generar token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(found);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][LOGIN] Generated token for User(id={}) logged in", found.getId());
    return token;
  }

  private User getUser(String normalizedEmail) {
    return userAdapterPort.findUserByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("[USER_AUTH_SERVICE][LOGIN] User(email={}) not found", normalizedEmail);
          return new com.store.arka.backend.domain.exception.UserNotFoundException(
              "User with email " + normalizedEmail + " not found");
        });
  }

  private void validateEmailExistence(String email) {
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email in register");
    if (userAdapterPort.existUserByEmail(normalizedEmail)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Email {} already exists for register a user", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " is already registered");
    }
  }

  private void validateUserNameExistence(String userName) {
    String normalizedUserName = ValidateAttributesUtils.throwIfValueNotAllowed(userName, "UserName in register");
    if (userAdapterPort.existUserByUserName(normalizedUserName)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Username={} already exists for register a User", normalizedUserName);
      throw new FieldAlreadyExistsException("Username " + normalizedUserName + " is already taken");
    }
  }
}
