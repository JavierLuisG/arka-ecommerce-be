package com.store.arka.backend.application.service;

import com.store.arka.backend.application.model.AuthResult;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.infrastructure.security.jwt.JwtService;
import com.store.arka.backend.infrastructure.security.UserDetailsImpl;
import com.store.arka.backend.application.port.in.IUserAuthUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.model.User;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public AuthResult registerCustomer(User user) {
    ValidateAttributesUtils.validateModel(user, "User");
    validateUserNameExistence(user.getUserName());
    validateEmailExistence(user.getEmail());
    // encriptar contraseña
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User created = User.registerCustomer(user.getUserName(), user.getEmail(), encodedPassword);
    User saved = userAdapterPort.saveCreateUser(created);
    log.info("[USER_AUTH_SERVICE][REGISTERED_CUSTOMER] Created new User(id={})", saved.getId());
    // generar el token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(saved);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][REGISTERED_CUSTOMER] Generated token for User(id={}) registered", saved.getId());
    return new AuthResult(saved, token);
  }

  @Override
  @Transactional
  public AuthResult registerStaffAccount(User user) {
    ValidateAttributesUtils.validateModel(user, "User");
    validateUserNameExistence(user.getUserName());
    validateEmailExistence(user.getEmail());
    // encriptar contraseña
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User created = User.createWithRole(user.getUserName(), user.getEmail(), encodedPassword, user.getRole());
    User saved = userAdapterPort.saveCreateUser(created);
    log.info("[USER_AUTH_SERVICE][REGISTERED_USER_ROLE] User(id={}) has created new User(id={}) whit role={}",
        securityUtils.getCurrentUserId(), saved.getId(), saved.getRole());
    // generar el token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(saved);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][REGISTER_USER_ROLE] Generated token for User(id={}) registered", saved.getId());
    return new AuthResult(saved, token);
  }

  @Override
  @Transactional
  public AuthResult login(User user) {
    ValidateAttributesUtils.validateModel(user, "User");
    // Autenticar usando email y contraseña
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
    User found = getUser(user.getEmail());
    // Generar token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(found);
    String token = jwtService.generateToken(userDetails);
    log.info("[USER_AUTH_SERVICE][LOGIN] Generated token for User(id={}) logged in", found.getId());
    return new AuthResult(found, token);
  }

  private User getUser(String email) {
    return userAdapterPort.findUserByEmail(email)
        .orElseThrow(() -> {
          log.warn("[USER_AUTH_SERVICE][LOGIN] User(email={}) not found", email);
          return new com.store.arka.backend.domain.exception.UserNotFoundException(
              "User with email " + email + " not found");
        });
  }

  private void validateEmailExistence(String email) {
    if (userAdapterPort.existsUserByEmail(email)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Email {} already exists for register a user", email);
      throw new FieldAlreadyExistsException("Email " + email + " is already registered");
    }
  }

  private void validateUserNameExistence(String userName) {
    if (userAdapterPort.existsUserByUserName(userName)) {
      log.warn("[USER_AUTH_SERVICE][REGISTER] Username={} already exists for register a User", userName);
      throw new FieldAlreadyExistsException("Username " + userName + " is already taken");
    }
  }
}
