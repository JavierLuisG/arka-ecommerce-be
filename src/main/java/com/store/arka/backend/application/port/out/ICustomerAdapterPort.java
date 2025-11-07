package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICustomerAdapterPort {
  Customer saveCreateCustomer(Customer customer);

  Customer saveUpdateCustomer(Customer customer);

  Optional<Customer> findCustomerById(UUID id);

  Optional<Customer> findCustomerByDocumentNumber(String number);

  List<Customer> findAllCustomers();

  List<Customer> findAllCustomersByStatus(CustomerStatus status);

  boolean existsCustomerByEmail(String email);
}
