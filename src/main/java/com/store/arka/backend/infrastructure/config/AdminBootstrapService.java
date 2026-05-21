package com.store.arka.backend.infrastructure.config;

import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.UserRole;
import com.store.arka.backend.domain.enums.UserStatus;
import com.store.arka.backend.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBootstrapService {
  private final IUserAdapterPort userAdapterPort;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void createAdminIfNotExist(String userName, String email, String password) {
    if (userAdapterPort.existsUserByRole(UserRole.ADMIN)) {
      log.info("[BOOTSTRAP] Admin already exists. Skipping.");
      return;
    }
    log.warn("[BOOTSTRAP] No ADMIN found. Creating INITIAL ADMIN...");
    User admin = new User(
        null,
        userName.trim().toLowerCase(),
        email.trim().toLowerCase(),
        passwordEncoder.encode(password),
        UserRole.ADMIN,
        UserStatus.ACTIVE,
        null,
        null
    );
    userAdapterPort.saveCreateUser(admin);
    log.warn("[BOOTSTRAP] >>> DEFAULT ADMIN CREATED <<<");
  }
}
