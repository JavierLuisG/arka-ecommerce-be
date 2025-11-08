package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICategoryUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
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
    ValidateAttributesUtils.throwIfModelNull(category, "Category");
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(category.getName(), "Category Name");
    String normalizedDescription = ValidateAttributesUtils.throwIfNullOrEmpty(category.getDescription(), "Description");
    if (categoryAdapterPort.existsCategoryByName(normalizedName)) {
      log.warn("[CATEGORY_SERVICE][CREATE] Category name '{}' already exists", normalizedName);
      throw new FieldAlreadyExistsException("Category with name " + normalizedName +
          " already exists. Choose a different name");
    }
    Category created = Category.create(normalizedName, normalizedDescription);
    Category saved = categoryAdapterPort.saveCategory(created);
    log.info("[CATEGORY_SERVICE][CREATE] Created new category {}, ID: {})", saved.getName(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Category getCategoryById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> {
          log.warn("[CATEGORY_SERVICE][GET_BY_ID] Category with ID {} not found", id);
          return new ModelNotFoundException("Category with ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Category getCategoryByName(String name) {
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(name, "Category Name");
    return categoryAdapterPort.findCategoryByName(normalizedName)
        .orElseThrow(() -> {
          log.warn("[CATEGORY_SERVICE][GET_BY_NAME] Category with name '{}' not found", normalizedName);
          return new ModelNotFoundException("Category with name " + normalizedName + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> getAllCategories() {
    log.info("[CATEGORY_SERVICE][GET_ALL] Fetching all categories");
    return categoryAdapterPort.findAllCategories();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> getAllCategoriesByStatus(CategoryStatus status) {
    log.info("[CATEGORY_SERVICE][GET_ALL_BY_STATUS] Fetching all categories with status {}", status);
    return categoryAdapterPort.findAllCategoriesByStatus(status);
  }

  @Override
  @Transactional
  public Category updateFieldsCategory(UUID id, Category category) {
    Category found = getCategoryById(id);
    found.update(category);
    Category saved = categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][UPDATE] Updated category ID {} with new description", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void softDeleteCategory(UUID id) {
    Category found = getCategoryById(id);
    found.delete();
    categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][DELETE] Category ID {} marked as deleted", id);
  }

  @Override
  @Transactional
  public Category restoreCategory(UUID id) {
    Category found = getCategoryById(id);
    found.restore();
    Category restored = categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][RESTORE] Category ID {} restored successfully", id);
    return restored;
  }
}
