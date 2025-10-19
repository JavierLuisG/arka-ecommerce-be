package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUpdater {
  private final CategoryMapper categoryMapper;

  public ProductEntity merge(ProductEntity entity, Product domain) {
    if (entity == null || domain == null) return entity;
    if (domain.getName() != null && !domain.getName().equals(entity.getName()))
      entity.setName(domain.getName());
    if (domain.getDescription() != null && !domain.getDescription().equals(entity.getDescription()))
      entity.setDescription(domain.getDescription());
    if (domain.getPrice() != null && !domain.getPrice().equals(entity.getPrice()))
      entity.setPrice(domain.getPrice());
    if (domain.getCategories() != null)
      entity.setCategories(categoryMapper.toReference(domain.getCategories()));
    if (domain.getStock() != null)
      entity.setStock(domain.getStock());
    if (domain.getStatus() != null && !domain.getStatus().equals(entity.getStatus()))
      entity.setStatus(domain.getStatus());
    return entity;
  }
}
