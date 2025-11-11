package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final String sku;
  private String name;
  private String description;
  private BigDecimal price;
  private Set<Category> categories;
  private Integer stock;
  private ProductStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Product create(String sku, String name, String description, BigDecimal price, Integer stock) {
    String normalizedSku = ValidateAttributesUtils.throwIfNullOrEmpty(sku, "SKU");
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(name, "Product Name");
    String normalizedDescription = ValidateAttributesUtils.throwIfNullOrEmpty(description, "Description");
    validatePrice(price);
    ProductStatus assignStatus = ProductStatus.ACTIVE;
    if (!validateStock(stock)) assignStatus = ProductStatus.EXHAUSTED;
    return new Product(
        null,
        normalizedSku,
        normalizedName,
        normalizedDescription,
        price,
        null,
        stock,
        assignStatus,
        null,
        null
    );
  }

  public void updateFields(String name, String description, BigDecimal price) {
    throwIfDeleted();
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(name, "Product Name");
    String normalizedDescription = ValidateAttributesUtils.throwIfNullOrEmpty(description, "Description");
    validatePrice(price);
    this.name = normalizedName;
    this.description = normalizedDescription;
    this.price = price;
  }

  public void updateCategories(Set<Category> categories) {
    throwIfDeleted();
    this.categories = categories;
  }

  public boolean isAvailableByStock(int quantity) {
    throwIfDeleted();
    return this.stock >= quantity && isNotDeleted();
  }

  public void decreaseStock(int quantity) {
    throwIfDeleted();
    validateAvailability(quantity);
    this.stock -= quantity;
    if (this.stock == 0) this.status = ProductStatus.EXHAUSTED;
  }

  public void increaseStock(int quantity) {
    throwIfDeleted();
    ValidateAttributesUtils.validateQuantity(quantity);
    this.stock += quantity;
    if (isExhausted()) this.status = ProductStatus.ACTIVE;
  }

  public boolean configurationThreshold() {
    return this.stock <= 20;
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Product is already marked as deleted");
    this.status = ProductStatus.ELIMINATED;
  }

  public void restore() {
    if (isNotDeleted()) throw new ModelActivationException("Product is already active and cannot be restored again");
    if (this.stock == 0) {
      this.status = ProductStatus.EXHAUSTED;
    } else {
      this.status = ProductStatus.ACTIVE;
    }
  }

  public void validateAvailability(int quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    throwIfDeleted();
    if (this.stock < quantity) {
      throw new QuantityBadRequestException("Product with id " + this.id + " does not have sufficient stock");
    }
  }

  public boolean isActive() {
    return this.status == ProductStatus.ACTIVE;
  }

  public boolean isExhausted() {
    return this.status == ProductStatus.EXHAUSTED;
  }

  public boolean isDeleted() {
    return this.status == ProductStatus.ELIMINATED;
  }

  public boolean isNotDeleted() {
    return isActive() || isExhausted();
  }

  public void throwIfDeleted() {
    if (isDeleted()) throw new ModelDeletionException("Product deleted previously");
  }

  private static boolean validateStock(Integer stock) {
    if (stock == null) throw new InvalidArgumentException("Stock cannot be null");
    if (stock < 0) throw new InvalidArgumentException("Stock cannot be less than 0");
    return stock > 0;
  }

  private static void validatePrice(BigDecimal price) {
    if (price == null) throw new InvalidArgumentException("Price cannot be null");
    if (price.signum() <= 0) throw new InvalidArgumentException("Price cannot be less than 0");
  }
}
