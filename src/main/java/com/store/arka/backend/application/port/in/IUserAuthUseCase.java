package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.model.User;

public interface IUserAuthUseCase {
  String register(User user);
  String login(User user);
}
