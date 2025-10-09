package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@AllArgsConstructor
public class Category {
  private final UUID id;
  private final String name;
  private String description;
  private CategoryStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Category create(String name, String description) {
    validateNotNullOrEmpty(name, "Name");
    return new Category(
        null,
        name,
        description,
        CategoryStatus.ACTIVE,
        null,
        null
    );
  }

  public void update(String description) {
    validateNotNullOrEmpty(description, "Description");
    this.description = description;
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidArgumentException(field + " cannot be null or empty");
    }
  }

  public boolean isNotDeleted() {
    return this.status == CategoryStatus.ACTIVE;
  }

  public void delete() {
    if (!isNotDeleted()) {
      throw new ModelDeletionException("Category already deleted previously");
    }
    this.status = CategoryStatus.ELIMINATED;
  }

  public void restore() {
    if (isNotDeleted()) {
      throw new ModelActivationException("Category already active previously");
    }
    this.status = CategoryStatus.ACTIVE;
  }
}
