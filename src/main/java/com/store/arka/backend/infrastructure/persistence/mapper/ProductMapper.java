package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
  private final CategoryMapper categoryMapper;

  public Product toDomain(ProductEntity entity) {
    if (entity == null) return null;
    return new Product(
        entity.getId(),
        entity.getSku(),
        entity.getName(),
        entity.getDescription(),
        entity.getPrice(),
        categoryMapper.toDomain(entity.getCategories()),
        entity.getStock(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public ProductEntity toCreateEntity(Product domain) {
    if (domain == null) return null;
    return new ProductEntity(
        domain.getId(),
        null,
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice(),
        categoryMapper.toEntity(domain.getCategories()),
        domain.getStock(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public ProductEntity toUpdateEntity(ProductEntity entity, Product domain) {
    if (entity == null || domain == null) return entity;
    if (domain.getName() != null && !domain.getName().equals(entity.getName()))
      entity.setName(domain.getName());
    if (!domain.getDescription().equals(entity.getDescription()))
      entity.setDescription(domain.getDescription());
    if (!domain.getPrice().equals(entity.getPrice()))
      entity.setPrice(domain.getPrice());
    if (domain.getCategories() != null)
      entity.setCategories(categoryMapper.toEntity(domain.getCategories()));
    if (domain.getStock() != null)
      entity.setStock(domain.getStock());
    if (domain.getStatus() != null && !domain.getStatus().equals(entity.getStatus()))
      entity.setStatus(domain.getStatus());
    return entity;
  }
}
