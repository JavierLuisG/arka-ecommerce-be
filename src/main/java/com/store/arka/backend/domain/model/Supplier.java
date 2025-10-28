package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private List<Product> products;
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
        new ArrayList<>(),
        SupplierStatus.ACTIVE,
        null,
        null
    );
  }

  public void updateFields(String commercialName, String contactName, String email, String phone, String taxId, String address, String city,
                           Country country) {
    if (!isActive()) throw new ModelDeletionException("Supplier already deleted previously");
    validateNotNullOrEmpty(commercialName, "Commercial name");
    validateNotNullOrEmpty(contactName, "Contact name");
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

  public boolean containsProduct(UUID productId) {
    return products.stream().anyMatch(product -> product.getId().equals(productId));
  }

  public void addProduct(Product product) {
    if (!isActive()) throw new ModelDeletionException("Supplier already deleted previously");
    if (product == null) throw new InvalidArgumentException("Product cannot be null");
    if (containsProduct(product.getId())) throw new InvalidArgumentException("Product already added to supplier");
    products.add(product);
  }

  public void removeProduct(Product product) {
    if (!isActive()) throw new ModelDeletionException("Supplier already deleted previously");
    if (!containsProduct(product.getId())) throw new InvalidArgumentException("Product not found in supplier");
    products.remove(product);
  }

  public void delete() {
    if (!isActive()) throw new ModelDeletionException("Supplier already deleted previously");
    this.status = SupplierStatus.ELIMINATED;
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
