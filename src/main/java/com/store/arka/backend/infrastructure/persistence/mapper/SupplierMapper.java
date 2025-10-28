package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.infrastructure.persistence.entity.SupplierEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SupplierMapper {
  private final ProductMapper productMapper;

  public Supplier toDomain(SupplierEntity entity) {
    if (entity == null) return null;
    return new Supplier(
        entity.getId(),
        entity.getCommercialName(),
        entity.getContactName(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getTaxId(),
        entity.getAddress(),
        entity.getCity(),
        entity.getCountry(),
        productMapper.toDomain(entity.getProducts()),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public SupplierEntity toEntity(Supplier domain) {
    if (domain == null) return null;
    SupplierEntity entity = new SupplierEntity(
        domain.getId(),
        domain.getCommercialName(),
        domain.getContactName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getTaxId(),
        domain.getAddress(),
        domain.getCity(),
        domain.getCountry(),
        new ArrayList<>(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
    if (domain.getProducts() != null) {
      entity.setProducts(productMapper.toEntity(domain.getProducts()));
    }
    return entity;
  }

  public SupplierEntity toReference(Supplier domain) {
    if (domain == null) return null;
    SupplierEntity entity = new SupplierEntity();
    entity.setId(domain.getId());
    return entity;
  }
}
