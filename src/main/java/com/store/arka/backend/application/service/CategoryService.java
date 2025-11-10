package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICategoryUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Category createCategory(Category category) {
    ValidateAttributesUtils.throwIfModelNull(category, "Category");
    validateCategoryNameExistence(category.getName());
    Category created = Category.create(category.getName(), category.getDescription());
    Category saved = categoryAdapterPort.saveCategory(created);
    log.info("[CATEGORY_SERVICE][CREATED] User(id={}) has created new category(name={}) and (id={})",
        securityUtils.getCurrentUserId(), saved.getName(), saved.getId());
    return saved;
  }

  private void validateCategoryNameExistence(String name) {
    ValidateAttributesUtils.throwIfValueNotAllowed(name, "Category Name");
    if (categoryAdapterPort.existsCategoryByName(name)) {
      log.warn("[CATEGORY_SERVICE][CREATED] Category(name={}) already exists", name);
      throw new FieldAlreadyExistsException("Category with name " + name + " already exists. Choose a different name");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Category getCategoryById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Category ID");
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> {
          log.warn("[CATEGORY_SERVICE][GET_BY_ID] Category(id={}) not found", id);
          return new ModelNotFoundException("Category ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Category getCategoryByName(String name) {
    String normalizedName = ValidateAttributesUtils.throwIfValueNotAllowed(name, "Category Name");
    return categoryAdapterPort.findCategoryByName(normalizedName)
        .orElseThrow(() -> {
          log.warn("[CATEGORY_SERVICE][GET_BY_NAME] Category(name={}) not found", normalizedName);
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
    log.info("[CATEGORY_SERVICE][GET_ALL_BY_STATUS] Fetching all categories with status=({})", status);
    return categoryAdapterPort.findAllCategoriesByStatus(status);
  }

  @Override
  @Transactional
  public Category updateDescription(UUID id, Category category) {
    Category found = getCategoryById(id);
    found.update(category);
    Category saved = categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][UPDATED] User(id={}) has updated category(id={}) with new description",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void softDeleteCategory(UUID id) {
    Category found = getCategoryById(id);
    found.delete();
    categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][DELETED] User(id={}) has marked as deleted Category(id={})",
        securityUtils.getCurrentUserId(), id);
  }

  @Override
  @Transactional
  public Category restoreCategory(UUID id) {
    Category found = getCategoryById(id);
    found.restore();
    Category restored = categoryAdapterPort.saveCategory(found);
    log.info("[CATEGORY_SERVICE][RESTORED] User(id={}) has restored Category(id={}) successfully",
        securityUtils.getCurrentUserId(), id);
    return restored;
  }
}
