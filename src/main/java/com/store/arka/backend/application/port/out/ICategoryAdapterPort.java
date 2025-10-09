package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICategoryAdapterPort {
  Category saveCategory(Category category);

  Optional<Category> findCategoryById(UUID id);

  Optional<Category> findCategoryByIdAndStatus(UUID id, CategoryStatus status);

  Optional<Category> findCategoryByName(String name);

  Optional<Category> findCategoryByNameAndStatus(String name, CategoryStatus status);

  List<Category> findAllCategories();

  List<Category> findAllCategoriesByStatus(CategoryStatus status);

  boolean existsCategoryByName(String name);
}
