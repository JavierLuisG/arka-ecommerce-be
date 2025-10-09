package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.ICustomerAdapterPort;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.persistence.mapper.CustomerMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements ICustomerAdapterPort {
  private final IJpaCustomerRepository jpaCustomerRepository;
  private final CustomerMapper mapper;

  @Override
  public Customer saveCreateCustomer(Customer customer) {
    return mapper.toDomain(jpaCustomerRepository.save(mapper.toCreateEntity(customer)));
  }

  @Override
  public Customer saveUpdateCustomer(Customer customer) {
    return jpaCustomerRepository.findById(customer.getId())
        .map(exists -> jpaCustomerRepository.save(mapper.toUpdateEntity(exists, customer)))
        .map(mapper::toDomain)
        .orElseThrow(() -> new ModelNotFoundException("Customer with id " + customer.getId() + " not found"));
  }

  @Override
  public Optional<Customer> findCustomerById(UUID id) {
    return jpaCustomerRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Customer> findCustomerByIdAndStatus(UUID id, CustomerStatus status) {
    return jpaCustomerRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Customer> findCustomerByDocumentNumber(String number) {
    return jpaCustomerRepository.findByDocumentNumber(number).map(mapper::toDomain);
  }

  @Override
  public Optional<Customer> findCustomerByDocumentNumberAndStatus(String number, CustomerStatus status) {
    return jpaCustomerRepository.findByDocumentNumberAndStatus(number, status).map(mapper::toDomain);
  }

  @Override
  public List<Customer> findAllCustomers() {
    return jpaCustomerRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Customer> findAllCustomersByStatus(CustomerStatus status) {
    return jpaCustomerRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsCustomerByDocumentNumber(String number) {
    return jpaCustomerRepository.existsByDocumentNumber(number);
  }

  @Override
  public boolean existsCustomerByEmail(String email) {
    return jpaCustomerRepository.existsByEmail(email);
  }
}
