package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

  public ProductEntity toEntity(Product domain) {
    if (domain == null) return null;
    return new ProductEntity(
        domain.getId(),
        null,
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice(),
        categoryMapper.toReference(domain.getCategories()),
        domain.getStock(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public ProductEntity toReference(UUID productId) {
    if (productId == null) return null;
    ProductEntity entity = new ProductEntity();
    entity.setId(productId);
    return entity;
  }
}
