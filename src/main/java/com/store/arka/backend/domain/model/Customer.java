package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final UUID userId;
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

  public static Customer create(UUID userId, Document document, String firstName, String lastName, String email, String phone,
                                String address, String city, Country country) {
    ValidateAttributesUtils.throwIfIdNull(userId, "User ID in Customer");
    ValidateAttributesUtils.throwIfModelNull(document, "Document in Customer");
    String normalizedFirstName = ValidateAttributesUtils.throwIfValueNotAllowed(firstName, "First name");
    String normalizedLastName = ValidateAttributesUtils.throwIfValueNotAllowed(lastName, "Last name");
    String normalizedEmail = ValidateAttributesUtils.throwIfNullOrEmpty(email, "Email");
    String normalizedPhone = ValidateAttributesUtils.throwIfNullOrEmpty(phone, "Phone");
    String normalizedAddress = ValidateAttributesUtils.throwIfNullOrEmpty(address, "Address");
    String normalizedCity = ValidateAttributesUtils.throwIfValueNotAllowed(city, "City");
    ValidateAttributesUtils.throwIfModelNull(country.toString(), "Country in Customer");
    return new Customer(
        null,
        userId,
        document,
        normalizedFirstName,
        normalizedLastName,
        normalizedEmail,
        normalizedPhone,
        normalizedAddress,
        normalizedCity,
        country,
        CustomerStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateFields(String firstName, String lastName, String email, String phone,
                           String address, String city, Country country) {
    throwIfDeleted();
    String normalizedFirstName = ValidateAttributesUtils.throwIfValueNotAllowed(firstName, "First name");
    String normalizedLastName = ValidateAttributesUtils.throwIfValueNotAllowed(lastName, "Last name");
    String normalizedEmail = ValidateAttributesUtils.throwIfNullOrEmpty(email, "Email");
    String normalizedPhone = ValidateAttributesUtils.throwIfNullOrEmpty(phone, "Phone");
    String normalizedAddress = ValidateAttributesUtils.throwIfNullOrEmpty(address, "Address");
    String normalizedCity = ValidateAttributesUtils.throwIfValueNotAllowed(city, "City");
    ValidateAttributesUtils.throwIfModelNull(country, "Country in Customer");
    this.firstName = normalizedFirstName;
    this.lastName = normalizedLastName;
    this.email = normalizedEmail;
    this.phone = normalizedPhone;
    this.address = normalizedAddress;
    this.city = normalizedCity;
    this.country = country;
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Customer is already marked as deleted");
    this.status = CustomerStatus.ELIMINATED;
  }

  public void restore() {
    if (isActive()) throw new ModelActivationException("Customer is already active and cannot be restored again");
    this.status = CustomerStatus.ACTIVE;
  }

  public boolean isActive() {
    return this.status == CustomerStatus.ACTIVE;
  }

  public boolean isDeleted() {
    return this.status == CustomerStatus.ELIMINATED;
  }

  public void throwIfDeleted() {
    if (isDeleted()) throw new ModelDeletionException("Customer deleted previously");
  }
}




