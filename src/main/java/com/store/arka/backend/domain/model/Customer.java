package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Customer {
  private final UUID id;
  private Document document;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String address;
  private String city;
  private Country country;
  private CustomerStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Customer create(Document document, String firstName, String lastName, String email, String phone,
                                String address, String city, Country country) {
    validateNotNullOrEmpty(firstName, "First name");
    validateNotNullOrEmpty(lastName, "Last name");
    validateNotNullOrEmpty(email, "Email");
    validateNotNullOrEmpty(phone, "Phone");
    validateNotNullOrEmpty(address, "Address");
    validateNotNullOrEmpty(city, "City");
    if (country == null) throw new InvalidArgumentException("Country cannot be null or empty");
    return new Customer(
        null,
        document,
        firstName,
        lastName,
        email,
        phone,
        address,
        city,
        country,
        CustomerStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateFields(String firstName, String lastName, String email, String phone,
                           String address, String city, Country country) {
    validateNotNullOrEmpty(firstName, "First name");
    validateNotNullOrEmpty(lastName, "Last name");
    validateNotNullOrEmpty(email, "Email");
    validateNotNullOrEmpty(address, "Address");
    validateNotNullOrEmpty(city, "City");
    validateNotNullOrEmpty(phone, "Phone");
    if (country == null) throw new InvalidArgumentException("Country cannot be null or empty");
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.city = city;
    this.country = country;
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) throw new InvalidArgumentException(field + " cannot be null or empty");
  }

  public boolean isActive() {
    return this.status == CustomerStatus.ACTIVE;
  }

  public void delete() {
    if (isActive()) {
      this.status = CustomerStatus.ELIMINATED;
    } else {
      throw new ModelDeletionException("Customer already deleted previously");
    }
  }

  public void restore() {
    if (this.status == CustomerStatus.ELIMINATED) {
      this.status = CustomerStatus.ACTIVE;
      this.updatedAt = LocalDateTime.now();
    } else {
      throw new ModelActivationException("Customer already active previously");
    }
  }
}




