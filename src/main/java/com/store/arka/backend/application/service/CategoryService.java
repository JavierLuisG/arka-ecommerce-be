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
    if (category == null) {
      log.warn("CategoryService, createCategory, cannot be null");
      throw new ModelNullException("Category cannot be null");
    }
    String normalizedName = category.getName().trim().toLowerCase();
    String normalizedDescription = category.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");

    if (forbiddenNames.contains(normalizedName)) {
      log.warn("CategoryService, createCategory, category name is not allowed");
      throw new InvalidArgumentException("This category name is not allowed");
    }
    if (categoryAdapterPort.existsCategoryByName(normalizedName)) {
      log.warn("CategoryService, createCategory, category name " + normalizedName + " already exists");
      throw new FieldAlreadyExistsException("Category name " + normalizedName + " already exists");
    }
    Category created = Category.create(normalizedName, normalizedDescription);
    return categoryAdapterPort.saveCategory(created);
  }

  @Override
  public Category getCategoryById(UUID id) {
    return categoryAdapterPort.findCategoryById(id)
        .orElseThrow(() -> {
          log.warn("CategoryService, getCategoryById, category with id " + id + " not found");
          throw new ModelNotFoundException("Category with id " + id + " not found");
        });
  }

  @Override
  public Category getCategoryByIdAndStatus(UUID id, CategoryStatus status) {
    Category found = getCategoryById(id);
    if (!found.getStatus().equals(status)) {
      log.warn("CategoryService, getCategoryByIdAndStatus, category with id " + id + " not " + status.toString());
      throw new ModelNotFoundException("Category with id " + id + " not " + status.toString());
    }
    return found;
  }

  @Override
  public Category getCategoryByName(String name) {
    return categoryAdapterPort.findCategoryByName(name)
        .filter(category -> category.getStatus().equals(CategoryStatus.ACTIVE))
        .orElseThrow(() -> {
          log.warn("CategoryService, getCategoryByName, product with name " + name + " not found");
          throw new ModelNotFoundException("Category with name " + name + " not found");
        });
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
    Category found = getCategoryByIdAndStatus(id, CategoryStatus.ACTIVE);
    String normalizedDescription = category.getDescription().trim();
    found.update(normalizedDescription);
    return categoryAdapterPort.saveCategory(found);
  }

  @Override
  public void deleteCategoryById(UUID id) {
    Category found = getCategoryById(id);
    found.delete();
    categoryAdapterPort.saveCategory(found);
  }

  @Override
  public Category restoreCategoryByName(String name) {
    Category found = categoryAdapterPort.findCategoryByName(name)
        .filter(category -> category.getStatus().equals(CategoryStatus.ELIMINATED))
        .orElseThrow(() -> {
          log.warn("CategoryService, restoreCategoryByName, category with name " + name + " not found");
          throw new ModelNotFoundException("Category with name " + name + " not found");
        });
    found.restore();
    return categoryAdapterPort.saveCategory(found);
  }
}
