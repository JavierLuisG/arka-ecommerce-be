package com.store.arka.backend.application.port.in;

public interface IEmailService {
  void sendNotificationEmail(String to, String subject, String message);
}
