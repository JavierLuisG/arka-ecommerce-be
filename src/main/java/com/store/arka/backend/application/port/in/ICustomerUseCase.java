package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Document;

import java.util.List;
import java.util.UUID;

public interface ICustomerUseCase {
  Customer createCustomer(Customer customer);

  Customer getCustomerById(UUID id);

  Customer getCustomerByIdAndStatus(UUID id, CustomerStatus status);

  Customer getCustomerByDocumentNumber(String number);

  Customer getCustomerByDocumentNumberAndStatus(String number, CustomerStatus status);

  List<Customer> getAllCustomers();

  List<Customer> getAllCustomersByStatus(CustomerStatus status);

  Customer updateFieldsCustomer(UUID id, Customer customer);

  void deleteCustomerById(UUID id);

  Customer restoreCustomerByDocumentNumber(String number);
}
