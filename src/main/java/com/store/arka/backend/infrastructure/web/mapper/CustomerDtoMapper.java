package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.web.dto.customer.request.CreateCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.request.UpdateFieldsCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDtoMapper {
  private final DocumentDtoMapper documentDtoMapper;

  public Customer toDomain(CreateCustomerDto dto) {
    return new Customer(
        null,
        documentDtoMapper.toDomain(dto.document()),
        dto.firstName(),
        dto.lastName(),
        dto.email(),
        dto.phone(),
        dto.address(),
        dto.city(),
        dto.country(),
        null,
        null,
        null
    );
  }

  public Customer toDomain(UpdateFieldsCustomerDto dto) {
    return new Customer(
        null,
        null,
        dto.firstName(),
        dto.lastName(),
        dto.email(),
        dto.phone(),
        dto.address(),
        dto.city(),
        dto.country(),
        null,
        null,
        null
    );
  }

  public CustomerResponseDto toDto(Customer domain) {
    return new CustomerResponseDto(
        domain.getId(),
        documentDtoMapper.toCustomerDto(domain.getDocument()),
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
}
