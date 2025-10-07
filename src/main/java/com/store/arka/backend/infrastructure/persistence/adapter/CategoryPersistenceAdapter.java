package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.infrastructure.persistence.mapper.CategoryMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements ICategoryAdapterPort {
  private final IJpaCategoryRepository jpaCategoryRepository;
  private final CategoryMapper mapper;

  @Override
  public Category saveCategory(Category category) {
    return mapper.toDomain(jpaCategoryRepository.save(mapper.toEntity(category)));
  }

  @Override
  public Optional<Category> findCategoryById(UUID id) {
    return jpaCategoryRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Category> findCategoryByName(String name) {
    return jpaCategoryRepository.findByName(name).map(mapper::toDomain);
  }

  @Override
  public List<Category> findAllCategories() {
    return jpaCategoryRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Category> findAllCategoriesByStatus(CategoryStatus status) {
    return jpaCategoryRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsCategoryByName(String name) {
    return jpaCategoryRepository.existsByName(name);
  }
}
