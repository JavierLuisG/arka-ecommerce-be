package com.store.arka.backend.infrastructure.persistence.updater;

import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerUpdater {
  public CustomerEntity merge(CustomerEntity entity, Customer domain) {
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
