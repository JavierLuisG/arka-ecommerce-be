package com.store.arka.backend.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer implements CommandLineRunner {
  private final AdminBootstrapService adminBootstrapService;

  @Value("${DEFAULT_ADMIN_USERNAME}")
  private String defaultAdminUsername;

  @Value("${DEFAULT_ADMIN_EMAIL}")
  private String defaultAdminEmail;

  @Value("${DEFAULT_ADMIN_PASSWORD}")
  private String defaultAdminPassword;

  @Override
  public void run(String... args) throws Exception {
    adminBootstrapService.createAdminIfNotExist(
        defaultAdminUsername,
        defaultAdminEmail,
        defaultAdminPassword
    );
  }
}
