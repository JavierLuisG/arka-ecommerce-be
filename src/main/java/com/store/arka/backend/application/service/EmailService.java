package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
  private final JavaMailSender mailSender;

  @Override
  public void sendNotificationEmail(String to, String subject, String message) {
    SimpleMailMessage mail = new SimpleMailMessage();
    mail.setTo(to);
    mail.setSubject(subject);
    mail.setText(message);
    mailSender.send(mail);
    log.info("[EMAIL_SERVICE][SEND_NOTIFICATION] Email sent to: {}", to);
  }
}
