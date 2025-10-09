package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface ICategoryUseCase {
  Category createCategory(Category category);

  Category getCategoryById(UUID id);

  Category getCategoryByIdAndStatus(UUID id, CategoryStatus status);

  Category getCategoryByName(String name);

  Category getCategoryByNameAndStatus(String name, CategoryStatus status);

  List<Category> getAllCategories();

  List<Category> getAllCategoriesByStatus(CategoryStatus status);

  Category updateFieldsCategory(UUID id, Category category);

  void deleteCategoryById(UUID id);

  Category restoreCategoryByName(String name);
}
