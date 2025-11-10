package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.persistence.entity.CustomerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
  private final DocumentMapper documentMapper;
  private final UserMapper userMapper;
  @PersistenceContext
  private EntityManager entityManager;

  public Customer toDomain(CustomerEntity entity) {
    if (entity == null) return null;
    return new Customer(
        entity.getId(),
        entity.getUser().getId(),
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

  public CustomerEntity toEntity(Customer domain) {
    if (domain == null) return null;
    return new CustomerEntity(
        domain.getId(),
        userMapper.toReference(domain.getUserId()),
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

  public CustomerEntity toReference(Customer domain) {
    if (domain == null) return null;
    return entityManager.getReference(CustomerEntity.class, domain.getId());
  }
}
