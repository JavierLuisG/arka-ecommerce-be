package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Supplier {
  private final UUID id;
  private String commercialName;
  private String contactName;
  private String email;
  private String phone;
  private String taxId;
  private String address;
  private String city;
  private Country country;
  private SupplierStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Supplier create(String commercialName, String contactName, String email, String phone, String taxId,
                                String address, String city, Country country) {
    validateNotNullOrEmpty(commercialName, "Commercial name");
    validateNotNullOrEmpty(contactName, "Contact name");
    validateNotNullOrEmpty(email, "Email");
    validateNotNullOrEmpty(phone, "Phone");
    validateNotNullOrEmpty(taxId, "Tax id");
    validateNotNullOrEmpty(address, "Address");
    validateNotNullOrEmpty(city, "City");
    if (country == null) throw new InvalidArgumentException("Country cannot be null or empty");
    return new Supplier(
        null,
        commercialName,
        contactName,
        email,
        phone,
        taxId,
        address,
        city,
        country,
        SupplierStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateFields(String commercialName, String contactName, String email, String phone, String taxId,String address, String city,
                           Country country) {
    validateNotNullOrEmpty(commercialName, "Commercial name");
    validateNotNullOrEmpty(email, "Email");
    validateNotNullOrEmpty(phone, "Phone");
    validateNotNullOrEmpty(taxId, "Tax id");
    validateNotNullOrEmpty(address, "Address");
    validateNotNullOrEmpty(city, "City");
    if (country == null) throw new InvalidArgumentException("Country cannot be null or empty");
    this.commercialName = commercialName;
    this.contactName = contactName;
    this.email = email;
    this.phone = phone;
    this.taxId = taxId;
    this.address = address;
    this.city = city;
    this.country = country;
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) throw new InvalidArgumentException(field + " cannot be null or empty");
  }

  public boolean isActive() {
    return this.status == SupplierStatus.ACTIVE;
  }

  public void delete() {
    if (isActive()) {
      this.status = SupplierStatus.ELIMINATED;
    } else {
      throw new ModelDeletionException("Supplier already deleted previously");
    }
  }

  public void restore() {
    if (this.status == SupplierStatus.ELIMINATED) {
      this.status = SupplierStatus.ACTIVE;
      this.updatedAt = LocalDateTime.now();
    } else {
      throw new ModelActivationException("Supplier already active previously");
    }
  }
}
