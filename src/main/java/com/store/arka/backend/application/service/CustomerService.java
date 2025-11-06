package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.ICustomerAdapterPort;
import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerUseCase {
  private final ICustomerAdapterPort customerAdapterPort;
  private final IDocumentUseCase documentUseCase;

  @Override
  @Transactional
  public Customer createCustomer(Customer customer) {
    if (customer == null) throw new ModelNullException("Customer cannot be null");
    String normalizedFirstName = customer.getFirstName().trim().toLowerCase();
    String normalizedLastName = customer.getLastName().trim().toLowerCase();
    String normalizedEmail = customer.getEmail().trim().toLowerCase();
    String normalizedAddress = customer.getAddress().trim().toLowerCase();
    String normalizedCity = customer.getCity().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, customer.getCountry().toString(), "Country");
    if (customerAdapterPort.existsCustomerByEmail(normalizedEmail)) {
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exist");
    }
    if (customerAdapterPort.existsCustomerByDocumentNumber(customer.getDocument().getNumber())) {
      throw new FieldAlreadyExistsException("Document number already exist");
    }
    Document document = documentUseCase.createDocument(customer.getDocument());
    Customer created = Customer.create(
        document,
        normalizedFirstName,
        normalizedLastName,
        normalizedEmail,
        customer.getPhone(),
        normalizedAddress,
        normalizedCity,
        normalizedCountry
    );
    return customerAdapterPort.saveCreateCustomer(created);
  }

  @Override
  public Customer getCustomerById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return customerAdapterPort.findCustomerById(id)
        .orElseThrow(() -> new ModelNotFoundException("Customer with id " + id + " not found"));
  }

  @Override
  public Customer getCustomerByIdAndStatus(UUID id, CustomerStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return customerAdapterPort.findCustomerByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Customer with id " + id + " and status " + status + " not found"));
  }

  @Override
  public Customer getCustomerByDocumentNumber(String number) {
    if (number == null || number.isBlank()) throw new InvalidArgumentException("Number is required");
    return customerAdapterPort.findCustomerByDocumentNumber(number)
        .orElseThrow(() -> new ModelNotFoundException("Customer with number " + number + " not found"));
  }

  @Override
  public Customer getCustomerByDocumentNumberAndStatus(String number, CustomerStatus status) {
    if (number == null || number.isBlank()) throw new InvalidArgumentException("Number is required");
    return customerAdapterPort.findCustomerByDocumentNumberAndStatus(number, status)
        .orElseThrow(() -> new ModelNotFoundException("Customer with number " + number + " and status " + status + " not found"));
  }

  @Override
  public List<Customer> getAllCustomers() {
    return customerAdapterPort.findAllCustomers();
  }

  @Override
  public List<Customer> getAllCustomersByStatus(CustomerStatus status) {
    return customerAdapterPort.findAllCustomersByStatus(status);
  }

  @Override
  @Transactional
  public Customer updateFieldsCustomer(UUID id, Customer customer) {
    if (customer == null) throw new ModelNullException("Customer cannot be null");
    Customer found = getCustomerByIdAndStatus(id, CustomerStatus.ACTIVE);
    String normalizedFirstName = customer.getFirstName().trim().toLowerCase();
    String normalizedLastName = customer.getLastName().trim().toLowerCase();
    String normalizedEmail = customer.getEmail().trim().toLowerCase();
    String normalizedAddress = customer.getAddress().trim().toLowerCase();
    String normalizedCity = customer.getCity().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, customer.getCountry().toString(), "Country");
    if (customerAdapterPort.existsCustomerByEmail(normalizedEmail)
        && !found.getEmail().equals(normalizedEmail)) {
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exist in Customer");
    }
    found.updateFields(
        normalizedFirstName,
        normalizedLastName,
        normalizedEmail,
        customer.getPhone(),
        normalizedAddress,
        normalizedCity,
        normalizedCountry
    );
    return customerAdapterPort.saveUpdateCustomer(found);
  }

  @Override
  @Transactional
  public void deleteCustomerById(UUID id) {
    Customer found = getCustomerByIdAndStatus(id, CustomerStatus.ACTIVE);
    found.delete();
    customerAdapterPort.saveUpdateCustomer(found);
    documentUseCase.deleteDocument(found.getDocument().getId());
  }

  @Override
  @Transactional
  public Customer restoreCustomerByDocumentNumber(String number) {
    Customer found = getCustomerByDocumentNumberAndStatus(number, CustomerStatus.ELIMINATED);
    found.restore();
    customerAdapterPort.saveUpdateCustomer(found);
    documentUseCase.restoreDocumentByNumber(number);
    return found;
  }
}
