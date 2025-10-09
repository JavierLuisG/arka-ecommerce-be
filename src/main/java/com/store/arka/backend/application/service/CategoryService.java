package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICategoryUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.Category;
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
  public Category getCategoryById(UUID id) {
    if (id == null) throw new InvalidArgumentException("Id is required");
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> new ModelNotFoundException("Category with id " + id + " not found"));
  }

  @Override
  public Category getCategoryByIdAndStatus(UUID id, CategoryStatus status) {
    if (id == null) throw new InvalidArgumentException("Id is required");
    return categoryAdapterPort.findCategoryByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Category with id " + id + " and status " + status + " not found"));
  }

  @Override
  public Category getCategoryByName(String name) {
    if (name == null || name.isBlank()) throw new InvalidArgumentException("Name is required");
    return categoryAdapterPort.findCategoryByName(name)
        .orElseThrow(() -> new ModelNotFoundException("Category with name " + name + " not found"));
  }

  @Override
  public Category getCategoryByNameAndStatus(String name, CategoryStatus status) {
    if (name == null || name.isBlank()) throw new InvalidArgumentException("Name is required");
    return categoryAdapterPort.findCategoryByNameAndStatus(name, status)
        .orElseThrow(() -> new ModelNotFoundException("Category with name " + name + " and status " + status + " not found"));
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryAdapterPort.findAllCategories();
  }

  @Override
  public List<Category> getAllCategoriesByStatus(CategoryStatus status) {
    return categoryAdapterPort.findAllCategoriesByStatus(status);
  }

  @Override
  public Category updateFieldsCategory(UUID id, Category category) {
    if (category == null) throw new ModelNullException("Category cannot be null");
    String normalizedDescription = category.getDescription().trim();
    Category found = getCategoryByIdAndStatus(id, CategoryStatus.ACTIVE);
    found.update(normalizedDescription);
    return categoryAdapterPort.saveCategory(found);
  }

  @Override
  public void deleteCategoryById(UUID id) {
    Category found = getCategoryByIdAndStatus(id, CategoryStatus.ACTIVE);
    found.delete();
    categoryAdapterPort.saveCategory(found);
  }

  @Override
  public Category restoreCategoryByName(String name) {
    Category found = getCategoryByNameAndStatus(name, CategoryStatus.ELIMINATED);
    found.restore();
    return categoryAdapterPort.saveCategory(found);
  }
}
