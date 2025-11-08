package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.ICustomerAdapterPort;
import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerUseCase {
  private final ICustomerAdapterPort customerAdapterPort;
  private final IDocumentUseCase documentUseCase;

  @Override
  @Transactional
  public Customer createCustomer(Customer customer) {
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer");
    ValidateAttributesUtils.throwIfModelNull(customer.getDocument(), "Document in Customer");
    String normalizedEmail = customer.getEmail().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, customer.getCountry().toString(), "Country");
    if (customerAdapterPort.existsCustomerByEmail(normalizedEmail)) {
      log.warn("[CUSTOMER_SERVICE][CREATED] Email {} already exists for creating a customer", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists. Choose a different");
    }
    Document documentCreated = documentUseCase.createDocument(customer.getDocument());
    Customer created = Customer.create(
        documentCreated,
        customer.getFirstName(),
        customer.getLastName(),
        normalizedEmail,
        customer.getPhone(),
        customer.getAddress(),
        customer.getCity(),
        normalizedCountry
    );
    Customer saved = customerAdapterPort.saveCreateCustomer(created);
    log.info("[CUSTOMER_SERVICE][CREATED] Created new customer ID {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Customer getCustomerById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Customer ID");
    return customerAdapterPort.findCustomerById(id)
        .orElseThrow(() -> {
          log.warn("[CUSTOMER_SERVICE][GET_BY_ID] Customer ID {} not found", id);
          return new ModelNotFoundException("Customer ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Customer getCustomerByDocumentNumber(String number) {
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Number");
    return customerAdapterPort.findCustomerByDocumentNumber(number)
        .orElseThrow(() -> {
          log.warn("[CUSTOMER_SERVICE][GET_BY_NUMBER] Customer with document number {} not found", normalizedNumber);
          return new ModelNotFoundException("Customer with document number " + number + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getAllCustomers() {
    log.info("[CUSTOMER_SERVICE][GET_ALL] Fetching all customers");
    return customerAdapterPort.findAllCustomers();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getAllCustomersByStatus(CustomerStatus status) {
    log.info("[CUSTOMER_SERVICE][GET_ALL_BY_STATUS] Fetching all customers with status {}", status);
    return customerAdapterPort.findAllCustomersByStatus(status);
  }

  @Override
  @Transactional
  public Customer updateFieldsCustomer(UUID id, Customer customer) {
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer");
    Customer found = getCustomerById(id);
    String normalizedEmail = customer.getEmail().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, customer.getCountry().toString(), "Country");
    if (customerAdapterPort.existsCustomerByEmail(normalizedEmail)) {
      log.warn("[CUSTOMER_SERVICE][UPDATED] Email {} already exists for updating a customer", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists. Choose a different");
    }
    found.updateFields(
        customer.getFirstName(),
        customer.getLastName(),
        normalizedEmail,
        customer.getPhone(),
        customer.getAddress(),
        customer.getCity(),
        normalizedCountry
    );
    Customer saved = customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][UPDATED] Updated customer ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void softDeleteCustomer(UUID id) {
    Customer found = getCustomerById(id);
    found.delete();
    customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][DELETED] Customer ID {} marked as deleted", id);
    documentUseCase.softDeleteDocument(found.getDocument().getId());
  }

  @Override
  @Transactional
  public Customer restoreCustomer(UUID id) {
    Customer found = getCustomerById(id);
    found.restore();
    customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][RESTORED] Customer ID {} restored successfully", id);
    documentUseCase.restoreDocument(found.getDocument().getId());
    return found;
  }
}
