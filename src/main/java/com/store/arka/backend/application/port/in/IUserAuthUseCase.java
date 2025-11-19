package com.store.arka.backend.application.port.in;

import com.store.arka.backend.application.model.AuthResult;
import com.store.arka.backend.domain.model.User;

public interface IUserAuthUseCase {
  AuthResult registerCustomer(User user);

  AuthResult registerStaffAccount(User user);

  AuthResult login(User user);
}
