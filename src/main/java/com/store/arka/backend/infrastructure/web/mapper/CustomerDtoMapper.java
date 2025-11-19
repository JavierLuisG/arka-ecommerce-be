package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.infrastructure.web.dto.customer.request.CreateCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.request.UpdateFieldsCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseToOrderDto;
import com.store.arka.backend.shared.util.NormalizationUtils;
import com.store.arka.backend.shared.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDtoMapper {
  private final DocumentDtoMapper documentDtoMapper;

  public Customer toDomain(CreateCustomerDto dto) {
    if (dto == null) return null;
    return new Customer(
        null,
        PathUtils.validateAndParseUUID(dto.userId()),
        documentDtoMapper.toDomain(dto.document()),
        NormalizationUtils.normalizeShortText(dto.firstName()),
        NormalizationUtils.normalizeShortText(dto.lastName()),
        NormalizationUtils.normalizeEmail(dto.email()),
        NormalizationUtils.normalizePhone(dto.phone()),
        NormalizationUtils.normalizeShortText(dto.address()),
        NormalizationUtils.normalizeShortText(dto.city()),
        PathUtils.validateEnumOrThrow(Country.class, dto.country(), "Country"),
        null,
        null,
        null
    );
  }

  public Customer toDomain(UpdateFieldsCustomerDto dto) {
    if (dto == null) return null;
    return new Customer(
        null,
        null,
        null,
        NormalizationUtils.normalizeShortText(dto.firstName()),
        NormalizationUtils.normalizeShortText(dto.lastName()),
        NormalizationUtils.normalizeEmail(dto.email()),
        NormalizationUtils.normalizePhone(dto.phone()),
        NormalizationUtils.normalizeShortText(dto.address()),
        NormalizationUtils.normalizeShortText(dto.city()),
        PathUtils.validateEnumOrThrow(Country.class, dto.country(), "Country"),
        null,
        null,
        null
    );
  }

  public CustomerResponseDto toDto(Customer domain) {
    if (domain == null) return null;
    return new CustomerResponseDto(
        domain.getId(),
        domain.getUserId(),
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

  public CustomerResponseToOrderDto toOrderDto(Customer domain) {
    if (domain == null) return null;
    return new CustomerResponseToOrderDto(
        domain.getId(),
        domain.getUserId(),
        documentDtoMapper.toCustomerDto(domain.getDocument()),
        domain.getFirstName(),
        domain.getLastName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getAddress(),
        domain.getCity(),
        domain.getCountry()
    );
  }
}
