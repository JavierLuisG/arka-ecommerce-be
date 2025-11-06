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
    if (category == null) throw new ModelNullException("Category cannot be null");
    String normalizedName = category.getName().trim().toLowerCase();
    String normalizedDescription = category.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");
    if (forbiddenNames.contains(normalizedName)) {
      throw new InvalidArgumentException("This category name is not allowed");
    }
    if (categoryAdapterPort.existsCategoryByName(normalizedName)) {
      throw new FieldAlreadyExistsException("Category name " + normalizedName + " already exists");
    }
    Category created = Category.create(normalizedName, normalizedDescription);
    return categoryAdapterPort.saveCategory(created);
  }

  @Override
  @Transactional
  public Category getCategoryById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> new ModelNotFoundException("Category with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Category getCategoryByIdAndStatus(UUID id, CategoryStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return categoryAdapterPort.findCategoryByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Category with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public Category getCategoryByName(String name) {
    if (name == null || name.isBlank()) throw new InvalidArgumentException("Name is required");
    String normalizedName = name.trim().toLowerCase();
    return categoryAdapterPort.findCategoryByName(normalizedName)
        .orElseThrow(() -> new ModelNotFoundException("Category with name " + normalizedName + " not found"));
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
    return categoryAdapterPort.findAllCategories();
  }

  @Override
  @Transactional
  public List<Category> getAllCategoriesByStatus(CategoryStatus status) {
    return categoryAdapterPort.findAllCategoriesByStatus(status);
  }

  @Override
  @Transactional
  public Category updateFieldsCategory(UUID id, Category category) {
    if (category == null) throw new ModelNullException("Category cannot be null");
    String normalizedDescription = category.getDescription().trim();
    Category found = getCategoryByIdAndStatus(id, CategoryStatus.ACTIVE);
    found.update(normalizedDescription);
    return categoryAdapterPort.saveCategory(found);
  }

  @Override
  @Transactional
  public void deleteCategoryById(UUID id) {
    Category found = getCategoryByIdAndStatus(id, CategoryStatus.ACTIVE);
    found.delete();
    categoryAdapterPort.saveCategory(found);
  }

  @Override
  @Transactional
  public Category restoreCategoryByName(String name) {
    Category found = getCategoryByNameAndStatus(name, CategoryStatus.ELIMINATED);
    found.restore();
    return categoryAdapterPort.saveCategory(found);
  }
}
