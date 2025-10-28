package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductMapper {
  private final CategoryMapper categoryMapper;
  @PersistenceContext
  private EntityManager entityManager;

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
    return entityManager.getReference(ProductEntity.class, productId);
  }

  public List<Product> toDomain(List<ProductEntity> listEntity) {
    if (listEntity == null) return Collections.emptyList();
    List<Product> productList = new ArrayList<>();
    listEntity.forEach(entity -> {
      productList.add(new Product(
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
      ));
    });
    return productList;
  }

  public List<ProductEntity> toEntity(List<Product> listDomain) {
    if (listDomain == null) return Collections.emptyList();
    List<ProductEntity> entities = new ArrayList<>();
    listDomain.forEach(domain -> {
      entities.add(new ProductEntity(
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
      ));
    });
    return entities;
  }
}
