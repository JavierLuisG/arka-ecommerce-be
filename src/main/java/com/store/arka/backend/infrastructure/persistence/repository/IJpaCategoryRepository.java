package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
  Optional<CategoryEntity> findByName(String name);

  List<CategoryEntity> findAllByStatus(CategoryStatus status);

  boolean existsByName(String name);
}
