package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.CategoryStatus;
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
public class Category {
  @EqualsAndHashCode.Include
  private final UUID id;
  private final String name;
  private String description;
  private CategoryStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Category create(String name, String description) {
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(name, "Category Name");
    String normalizedDescription = ValidateAttributesUtils.throwIfNullOrEmpty(description, "Description");
    ValidateAttributesUtils.throwIfNullOrEmpty(description, "Description");
    return new Category(
        null,
        normalizedName,
        normalizedDescription,
        CategoryStatus.ACTIVE,
        null,
        null
    );
  }

  public void update(Category category) {
    if (isDeleted()) throw new ModelDeletionException("Category deleted previously");
    ValidateAttributesUtils.throwIfModelNull(category, "Category");
    this.description = ValidateAttributesUtils.throwIfNullOrEmpty(description, "Description");
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Category is already marked as deleted");
    this.status = CategoryStatus.ELIMINATED;
  }

  public void restore() {
    if (isActive()) throw new ModelActivationException("Category is already active and cannot be restored again");
    this.status = CategoryStatus.ACTIVE;
  }

  public boolean isDeleted() {
    return this.status == CategoryStatus.ELIMINATED;
  }

  public boolean isActive() {
    return this.status == CategoryStatus.ACTIVE;
  }
}
