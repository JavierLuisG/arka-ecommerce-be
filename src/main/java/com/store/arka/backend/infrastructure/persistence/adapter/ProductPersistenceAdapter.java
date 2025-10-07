package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IProductAdapterPort;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.ProductMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements IProductAdapterPort {
  private final IJpaProductRepository jpaProductRepository;
  private final ProductMapper mapper;

  @Override
  public Product saveProduct(Product product) {
    ProductEntity entity;
    if (product.getId() != null) {
      entity = jpaProductRepository.findById(product.getId())
          .map(exists -> mapper.toUpdateEntity(exists, product))
          .orElseThrow(() -> new ModelNotFoundException("Product with id " + product.getId() + " not found"));
    } else {
      entity = mapper.toCreateEntity(product);
    }
    return mapper.toDomain(jpaProductRepository.save(entity));
  }

  @Override
  public Optional<Product> findProductById(UUID id) {
    return jpaProductRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findProductBySku(String sku) {
    return jpaProductRepository.findBySku(sku).map(mapper::toDomain);
  }

  @Override
  public List<Product> findAllProducts() {
    return jpaProductRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Product> findAllProductsByStatus(ProductStatus status) {
    return jpaProductRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsProductBySku(String sku) {
    return jpaProductRepository.existsBySku(sku);
  }
}
