package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.entity.SupplierEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SupplierUpdater {
  private final ProductMapper productMapper;

  public SupplierEntity merge(SupplierEntity entity, Supplier domain) {
    if (entity == null || domain == null)
      throw new InvalidArgumentException("Entity and domain are required");
    if (!entity.getCommercialName().equals(domain.getCommercialName()))
      entity.setCommercialName(domain.getCommercialName());
    if (!entity.getContactName().equals(domain.getContactName()))
      entity.setContactName(domain.getContactName());
    if (!entity.getEmail().equals(domain.getEmail()))
      entity.setEmail(domain.getEmail());
    if (!entity.getPhone().equals(domain.getPhone()))
      entity.setPhone(domain.getPhone());
    if (!entity.getTaxId().equals(domain.getTaxId()))
      entity.setTaxId(domain.getTaxId());
    if (!entity.getAddress().equals(domain.getAddress()))
      entity.setAddress(domain.getAddress());
    if (!entity.getCity().equals(domain.getCity()))
      entity.setCity(domain.getCity());
    if (!entity.getCountry().equals(domain.getCountry()))
      entity.setCountry(domain.getCountry());
    if (!entity.getStatus().equals(domain.getStatus()))
      entity.setStatus(domain.getStatus());

    List<ProductEntity> domainProducts = domain.getProducts().stream()
        .map(product -> productMapper.toReference(product.getId()))
        .collect(Collectors.toList());

    entity.setProducts(domainProducts);
    return entity;
  }
}
