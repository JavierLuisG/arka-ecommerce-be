package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.persistence.entity.CategoryEntity;
import com.store.arka.backend.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class CategoryMapper {
  public Category toDomain(CategoryEntity entity) {
    if (entity == null) return null;
    return new Category(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public CategoryEntity toEntity(Category domain) {
    if (domain == null) return null;
    return new CategoryEntity(
        domain.getId(),
        domain.getName(),
        domain.getDescription(),
        null,
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public Set<CategoryEntity> toReference(Set<Category> domain) {
    if (domain == null) return null;
    Set<CategoryEntity> entities = new HashSet<>();
    domain.forEach(category -> {
      CategoryEntity entity = new CategoryEntity();
      entity.setId(category.getId());
      entities.add(entity);
    });
    return entities;
  }

  public Set<Category> toDomain(Set<CategoryEntity> listEntity) {
    if (listEntity == null) {
      return Collections.emptySet();
    }
    Set<Category> listCategory = new HashSet<>();
    listEntity.forEach(entity -> {
      listCategory.add(new Category(
          entity.getId(),
          entity.getName(),
          entity.getDescription(),
          entity.getStatus(),
          entity.getCreatedAt(),
          entity.getUpdatedAt()
      ));
    });
    return listCategory;
  }

  public Set<CategoryEntity> toEntity(Set<Category> listDomain) {
    if (listDomain == null) {
      return Collections.emptySet();
    }
    Set<CategoryEntity> listEntity = new HashSet<>();
    listDomain.forEach(domain -> {
      listEntity.add(new CategoryEntity(
          domain.getId(),
          domain.getName(),
          domain.getDescription(),
          null,
          domain.getStatus(),
          domain.getCreatedAt(),
          domain.getUpdatedAt()
      ));
    });
    return listEntity;
  }
}
