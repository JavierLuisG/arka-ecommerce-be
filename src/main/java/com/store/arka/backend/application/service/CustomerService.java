package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.ICustomerAdapterPort;
import com.store.arka.backend.application.port.out.IUserAdapterPort;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final IUserAdapterPort userAdapterPort;
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Customer createCustomer(Customer customer) {
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer");
    ValidateAttributesUtils.throwIfIdNull(customer.getUserId(), "User ID in Customer");
    validateUserExistence(customer);
    ValidateAttributesUtils.throwIfModelNull(customer.getDocument(), "Document in Customer");
    validateUserIdUniqueness(customer.getUserId());
    validateEmailUniqueness(customer.getEmail(), null);
    Document documentCreated = documentUseCase.createDocument(customer.getDocument());
    Customer created = Customer.create(
        customer.getUserId(),
        documentCreated,
        customer.getFirstName(),
        customer.getLastName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getAddress(),
        customer.getCity(),
        customer.getCountry()
    );
    Customer saved = customerAdapterPort.saveCreateCustomer(created);
    log.info("[CUSTOMER_SERVICE][CREATED] User {} created new customer ID {}",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  private void validateUserIdUniqueness(UUID userId) {
    if (customerAdapterPort.existsCustomerByUserId(userId)) {
      log.warn("[CUSTOMER_SERVICE][CREATED] User ID {} already exists for creating a customer", userId);
      throw new FieldAlreadyExistsException("User ID " + userId + " already exists. Choose a different");
    }
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
  public Customer getCustomerByIdSecure(UUID id) {
    Customer found = getCustomerById(id);
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public Customer getCustomerByUserId(UUID userId) {
    ValidateAttributesUtils.throwIfIdNull(userId, "User ID in Customer");
    Customer found = customerAdapterPort.findCustomerByUserId(userId)
        .orElseThrow(() -> {
          log.warn("[CUSTOMER_SERVICE][GET_BY_USER] Customer with user ID {} not found", userId);
          return new ModelNotFoundException("Customer with user ID" + userId + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public Customer getCustomerByDocumentNumber(String number) {
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Number");
    Customer found = customerAdapterPort.findCustomerByDocumentNumber(number)
        .orElseThrow(() -> {
          log.warn("[CUSTOMER_SERVICE][GET_BY_NUMBER] Customer with document number {} not found", normalizedNumber);
          return new ModelNotFoundException("Customer with document number " + number + " not found");
        });
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN", "MANAGER");
    return found;
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
    Customer found = getCustomerById(id);
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN");
    ValidateAttributesUtils.throwIfModelNull(customer, "Customer");
    validateEmailUniqueness(customer.getEmail(), found.getEmail());
    found.updateFields(
        customer.getFirstName(),
        customer.getLastName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getAddress(),
        customer.getCity(),
        customer.getCountry()
    );
    Customer saved = customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][UPDATED] User {} Updated customer ID {} ",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void softDeleteCustomer(UUID id) {
    Customer found = getCustomerById(id);
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN");
    found.delete();
    customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][DELETED] User {} Customer ID {} marked as deleted",
        securityUtils.getCurrentUserId(), id);
    documentUseCase.softDeleteDocument(found.getDocument().getId());
  }

  @Override
  @Transactional
  public Customer restoreCustomer(UUID id) {
    Customer found = getCustomerById(id);
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN");
    found.restore();
    customerAdapterPort.saveUpdateCustomer(found);
    log.info("[CUSTOMER_SERVICE][RESTORED] User {} Customer ID {} restored successfully",
        securityUtils.getCurrentUserId(), id);
    documentUseCase.restoreDocument(found.getDocument().getId());
    return found;
  }

  private void validateUserExistence(Customer customer) {
    if (!userAdapterPort.existsUserById(customer.getUserId())) {
      log.warn("[CUSTOMER_SERVICE][CREATED] User ID {} not found in database", customer.getUserId());
      throw new ModelNotFoundException("User ID " + customer.getUserId() + " not found in database");
    }
  }

  private void validateEmailUniqueness(String newEmail, String oldEmail) {
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(newEmail, "Email in Customer");
    boolean exists = customerAdapterPort.existsCustomerByEmail(normalizedEmail);
    if (oldEmail == null && exists) {
      log.warn("[CUSTOMER_SERVICE][CREATED] Email {} already exists for creating a customer", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists. Choose a different");
    }
    if (exists && !oldEmail.equals(normalizedEmail)) {
      log.warn("[CUSTOMER_SERVICE][UPDATED] Email {} already exists for updating a customer", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists. Choose a different");
    }
  }
}
