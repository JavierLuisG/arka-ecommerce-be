package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaCustomerRepository extends JpaRepository<CustomerEntity, UUID> {
  Optional<CustomerEntity> findByIdAndStatus(UUID id, CustomerStatus status);

  Optional<CustomerEntity> findByDocumentNumber(String number);

  Optional<CustomerEntity> findByDocumentNumberAndStatus(String number, CustomerStatus status);

  List<CustomerEntity> findAllByStatus(CustomerStatus status);

  boolean existsByDocumentNumber(String number);

  boolean existsByEmail(String email);
}
