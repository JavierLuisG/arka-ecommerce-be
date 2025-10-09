package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Getter
@AllArgsConstructor
public class Product {
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
    validateNotNullOrEmpty(name, "Name");
    validateNotNullOrEmpty(description, "Description");
    validateNotNullOrEmpty(sku, "SKU");
    validatePrice(price);
    ProductStatus assignStatus = ProductStatus.ACTIVE;
    if (!validateStock(stock)) {
      assignStatus = ProductStatus.EXHAUSTED;
    }
    return new Product(
        null,
        sku,
        name,
        description,
        price,
        null,
        stock,
        assignStatus,
        null,
        null
    );
  }

  public void updateFields(String name, String description, BigDecimal price) {
    validateNotNullOrEmpty(name, "Name");
    validateNotNullOrEmpty(description, "Description");
    validatePrice(price);
    this.name = name;
    this.description = description;
    this.price = price;
  }

  public void updateCategories(Set<Category> categories) {
    this.categories = categories;
  }

  private static boolean validateStock(Integer stock) {
    if (stock == null) {
      throw new InvalidArgumentException("Stock cannot be null");
    }
    if (stock < 0) {
      throw new InvalidArgumentException("Stock cannot be less than 0");
    }
    return stock > 0;
  }

  private static void validatePrice(BigDecimal price) {
    if (price == null) {
      throw new InvalidArgumentException("Price cannot be null");
    }
    if (price.signum() <= 0) {
      throw new InvalidArgumentException("Price cannot be less than 0");
    }
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidArgumentException(field + " cannot be null or empty");
    }
  }

  public boolean isNotDeleted() {
    return this.status == ProductStatus.ACTIVE || this.status == ProductStatus.EXHAUSTED;
  }

  public boolean isAvailable(int quantity) {
    return this.stock >= quantity && isNotDeleted();
  }

  public void validateAvailability(int quantity) {
    if (quantity <= 0) {
      throw new QuantityBadRequestException("Quantity must be greater than 0");
    }
    if (this.stock < quantity) {
      throw new QuantityBadRequestException("Stock must be greater than or equal to quantity");
    }
    if (!isNotDeleted()) {
      throw new ModelNotAvailableException("Product is not active");
    }
  }

  public void decreaseStock(int quantity) {
    validateAvailability(quantity);
    this.stock -= quantity;
    if (this.stock == 0) {
      this.status = ProductStatus.EXHAUSTED;
    }
  }

  public void increaseStock(int quantity) {
    if (this.status == ProductStatus.ELIMINATED) {
      throw new ModelDeletionException("Product deleted previously");
    }
    if (quantity <= 0) {
      throw new QuantityBadRequestException("Quantity must be greater than 0");
    }
    this.stock += quantity;
    if (this.status == ProductStatus.EXHAUSTED) {
      this.status = ProductStatus.ACTIVE;
    }
  }

  public boolean lowStock() {
    return this.stock <= 20;
  }

  public void delete() {
    if (!isNotDeleted()) {
      throw new ModelDeletionException("Product already deleted previously");
    }
    this.status = ProductStatus.ELIMINATED;
  }

  public void restore() {
    if (isNotDeleted()) {
      throw new ModelActivationException("Product already active previously");
    }
    if (this.stock == 0) {
      this.status = ProductStatus.EXHAUSTED;
    } else {
      this.status = ProductStatus.ACTIVE;
    }
  }
}
