package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICategoryUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryUseCase {
  private final ICategoryAdapterPort categoryAdapterPort;

  @Override
  @Transactional
  public Category createCategory(Category category) {
    if (category == null) {
      log.warn("Attempted to create a null category");
      throw new ModelNullException("Category request body cannot be null");
    }
    String normalizedName = category.getName().trim().toLowerCase();
    String normalizedDescription = category.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");
    if (forbiddenNames.contains(normalizedName)) {
      log.warn("Invalid category name attempted: {}", normalizedName);
      throw new InvalidArgumentException("Category name " + normalizedName + " is not allowed");
    }
    if (categoryAdapterPort.existsCategoryByName(normalizedName)) {
      log.warn("Category name '{}' already exists", normalizedName);
      throw new FieldAlreadyExistsException("Category with name " + normalizedName +
          " already exists. Choose a different name");
    }
    Category created = Category.create(normalizedName, normalizedDescription);
    Category saved = categoryAdapterPort.saveCategory(created);
    log.info("Created new category: {} (ID: {})", saved.getName(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Category getCategoryById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> {
          log.warn("Category with ID {} not found", id);
          return new ModelNotFoundException("Category with id " + id + " not found");
        });
  }

  @Override
  @Transactional
  public Category getCategoryByName(String name) {
    if (name == null || name.isBlank()) {
      log.warn("Category name is null or blank");
      throw new InvalidArgumentException("Name is required");
    }
    String normalizedName = name.trim().toLowerCase();
    return categoryAdapterPort.findCategoryByName(normalizedName)
        .orElseThrow(() -> {
          log.warn("Category with name '{}' not found", normalizedName);
          return new ModelNotFoundException("Category with name " + normalizedName + " not found");
        });
  }

  @Override
  @Transactional
  public Category getCategoryByNameAndStatus(String name, CategoryStatus status) {
    if (name == null || name.isBlank()) throw new InvalidArgumentException("Name is required");
    String normalizedName = name.trim().toLowerCase();
    return categoryAdapterPort.findCategoryByNameAndStatus(normalizedName, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Category with name " + normalizedName + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Category> getAllCategories() {
    log.info("Fetching all categories");
    return categoryAdapterPort.findAllCategories();
  }

  @Override
  @Transactional
  public List<Category> getAllCategoriesByStatus(CategoryStatus status) {
    log.info("Fetching all categories with status {}", status);
    return categoryAdapterPort.findAllCategoriesByStatus(status);
  }

  @Override
  @Transactional
  public Category updateFieldsCategory(UUID id, Category category) {
    Category found = getCategoryById(id);
    found.update(category);
    log.info("Updated category ID {} with new description", id);
    return categoryAdapterPort.saveCategory(found);
  }

  @Override
  @Transactional
  public void deleteCategory(UUID id) {
    Category found = getCategoryById(id);
    found.delete();
    categoryAdapterPort.saveCategory(found);
    log.info("Category ID {} marked as deleted", id);
  }

  @Override
  @Transactional
  public Category restoreCategory(UUID id) {
    Category found = getCategoryById(id);
    found.restore();
    Category restored = categoryAdapterPort.saveCategory(found);
    log.info("Category ID {} restored successfully", id);
    return restored;
  }
}
