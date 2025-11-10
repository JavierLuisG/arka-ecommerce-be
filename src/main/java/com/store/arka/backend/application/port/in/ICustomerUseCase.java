package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.model.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerUseCase {
  Customer createCustomer(Customer customer);

  Customer getCustomerById(UUID id);

  Customer getCustomerByUserId(UUID userId);

  Customer getCustomerByDocumentNumber(String number);

  List<Customer> getAllCustomers();

  List<Customer> getAllCustomersByStatus(CustomerStatus status);

  Customer updateFieldsCustomer(UUID id, Customer customer);

  void softDeleteCustomer(UUID id);

  Customer restoreCustomer(UUID id);
}
