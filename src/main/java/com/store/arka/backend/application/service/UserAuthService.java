package com.store.arka.backend.application.service;

import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.infrastructure.security.jwt.JwtService;
import com.store.arka.backend.infrastructure.security.UserDetailsImpl;
import com.store.arka.backend.application.port.in.IUserAuthUseCase;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.model.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    if (user == null) throw new InvalidArgumentException("User cannot be null");
    String normalizedUserName = user.getUserName().trim().toLowerCase();
    String normalizedEmail = user.getEmail().trim().toLowerCase();
    if (userAdapterPort.existUserByUserName(normalizedUserName)) {
      throw new FieldAlreadyExistsException("Username " + user.getUserName() + " is already taken");
    }
    if (userAdapterPort.existUserByEmail(normalizedEmail)) {
      throw new FieldAlreadyExistsException("Email " + user.getEmail() + " is already registered");
    }
    // crear y guardar el usuario con contraseña encriptada
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    User created = User.create(normalizedUserName, normalizedEmail, encodedPassword);
    User saved = userAdapterPort.saveCreateUser(created);
    // generar el token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(saved);
    return jwtService.generateToken(userDetails);
  }

  @Override
  @Transactional
  public String login(User user) {
    if (user == null) throw new InvalidArgumentException("User cannot be null");
    String normalizedEmail = user.getEmail().trim().toLowerCase();
    // Autenticar usando email y contraseña
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, user.getPassword()));
    // Obtener el usuario desde base de datos
    User found = userAdapterPort.findUserByEmail(normalizedEmail)
        .orElseThrow(() -> new  com.store.arka.backend.domain.exception.UserNotFoundException(
            "User with email " + normalizedEmail + " not found"));
    // Generar token JWT
    UserDetailsImpl userDetails = new UserDetailsImpl(found);
    return jwtService.generateToken(userDetails);
  }
}
