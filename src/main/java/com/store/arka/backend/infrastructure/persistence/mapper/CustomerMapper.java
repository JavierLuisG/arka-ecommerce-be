package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.persistence.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
  private final DocumentMapper documentMapper;

  public Customer toDomain(CustomerEntity entity) {
    if (entity == null) return null;
    return new Customer(
        entity.getId(),
        documentMapper.toDomain(entity.getDocument()),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getAddress(),
        entity.getCity(),
        entity.getCountry(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public CustomerEntity toCreateEntity(Customer domain) {
    if (domain == null) return null;
    return new CustomerEntity(
        domain.getId(),
        documentMapper.toEntity(domain.getDocument()),
        domain.getFirstName(),
        domain.getLastName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getAddress(),
        domain.getCity(),
        domain.getCountry(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public CustomerEntity toUpdateEntity(CustomerEntity entity, Customer domain) {
    if (entity == null || domain == null) return entity;
    if (domain.getFirstName() != null && !domain.getFirstName().equals(entity.getFirstName()))
      entity.setFirstName(domain.getFirstName());
    if (domain.getLastName() != null && !domain.getLastName().equals(entity.getLastName()))
      entity.setLastName(domain.getLastName());
    if (domain.getEmail() != null && !domain.getEmail().equals(entity.getEmail()))
      entity.setEmail(domain.getEmail());
    if (domain.getPhone() != null && !domain.getPhone().equals(entity.getPhone()))
      entity.setPhone(domain.getPhone());
    if (domain.getAddress() != null && !domain.getAddress().equals(entity.getAddress()))
      entity.setAddress(domain.getAddress());
    if (domain.getCity() != null && !domain.getCity().equals(entity.getCity()))
      entity.setCity(domain.getCity());
    if (domain.getCountry() != null && !domain.getCountry().equals(entity.getCountry()))
      entity.setCountry(domain.getCountry());
    if (domain.getStatus() != null && !domain.getStatus().equals(entity.getStatus()))
      entity.setStatus(domain.getStatus());
    return entity;
  }
}
