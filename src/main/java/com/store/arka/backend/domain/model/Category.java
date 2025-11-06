package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import com.store.arka.backend.domain.exception.ModelNullException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final String name;
  private String description;
  private CategoryStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Category create(String name, String description) {
    validateNotNullOrEmpty(name, "Name");
    validateNotNullOrEmpty(description, "Description");
    String normalizeName = normalizeName(name);
    String normalizeDescription = normalizeText(description);
    return new Category(
        null,
        normalizeName,
        normalizeDescription,
        CategoryStatus.ACTIVE,
        null,
        null
    );
  }

  public void update(Category category) {
    if (category == null) throw new ModelNullException("Category cannot be null");
    validateNotNullOrEmpty(category.getDescription(), "Description");
    String description = normalizeText(category.getDescription());
    if (isDeleted()) throw new ModelDeletionException("Category already deleted previously");
    this.description = description;
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Category is already marked as deleted");
    this.status = CategoryStatus.ELIMINATED;
  }

  public boolean isDeleted() {
    return this.status == CategoryStatus.ELIMINATED;
  }

  public boolean isActive() {
    return this.status == CategoryStatus.ACTIVE;
  }

  public void restore() {
    if (isActive()) throw new ModelActivationException("Category is already active and cannot be restored again");
    this.status = CategoryStatus.ACTIVE;
  }

  private static String normalizeText(String value) {
    return value.trim();
  }

  private static String normalizeName(String value) {
    return value.toLowerCase().trim();
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) throw new InvalidArgumentException(field + " cannot be null or empty");
  }
}
