package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Supplier {
  @EqualsAndHashCode.Include
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
    ValidateAttributesUtils.validateValueNotAllowed(commercialName, "Commercial name");
    ValidateAttributesUtils.validateValueNotAllowed(contactName, "Contact name");
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email");
    ValidateAttributesUtils.validateValueNotAllowed(phone, "Phone");
    ValidateAttributesUtils.validateValueNotAllowed(taxId, "Tax id");
    ValidateAttributesUtils.validateValueNotAllowed(address, "Address");
    ValidateAttributesUtils.validateValueNotAllowed(city, "City");
    ValidateAttributesUtils.validateModel(country, "Country in Supplier");
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
    throwIfDeleted();
    ValidateAttributesUtils.validateValueNotAllowed(commercialName, "Commercial name");
    ValidateAttributesUtils.validateValueNotAllowed(contactName, "Contact name");
    ValidateAttributesUtils.validateValueNotAllowed(email, "Email");
    ValidateAttributesUtils.validateValueNotAllowed(phone, "Phone");
    ValidateAttributesUtils.validateValueNotAllowed(taxId, "Tax id");
    ValidateAttributesUtils.validateValueNotAllowed(address, "Address");
    ValidateAttributesUtils.validateValueNotAllowed(city, "City");
    ValidateAttributesUtils.validateModel(country, "Country in Supplier");
    this.commercialName = commercialName;
    this.contactName = contactName;
    this.email = email;
    this.phone = phone;
    this.taxId = taxId;
    this.address = address;
    this.city = city;
    this.country = country;
  }

  public boolean containsProduct(UUID productId) {
    return products.stream().anyMatch(product -> product.getId().equals(productId));
  }

  public void addProduct(Product product) {
    throwIfDeleted();
    ValidateAttributesUtils.validateModel(product, "Product in Supplier");
    if (containsProduct(product.getId())) throw new InvalidArgumentException("Product already added to supplier");
    products.add(product);
  }

  public void removeProduct(Product product) {
    throwIfDeleted();
    if (!containsProduct(product.getId())) throw new InvalidArgumentException("Product not found in supplier");
    products.remove(product);
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Supplier already deleted previously");
    this.status = SupplierStatus.ELIMINATED;
  }

  public void restore() {
    if (isActive()) throw new ModelActivationException("Supplier is already active and cannot be restored again");
    this.status = SupplierStatus.ACTIVE;
  }

  public boolean isActive() {
    return this.status == SupplierStatus.ACTIVE;
  }

  public boolean isDeleted() {
    return this.status == SupplierStatus.ELIMINATED;
  }

  public void throwIfDeleted() {
    if (isDeleted()) throw new ModelDeletionException("Supplier deleted previously");
  }
}
