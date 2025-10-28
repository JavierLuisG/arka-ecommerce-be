package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.infrastructure.web.dto.supplier.request.SupplierDto;
import com.store.arka.backend.infrastructure.web.dto.supplier.response.SupplierResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplierDtoMapper {
  private final ProductDtoMapper productDtoMapper;

  public Supplier toDomain(SupplierDto dto) {
    return new Supplier(
        null,
        dto.commercialName(),
        dto.contactName(),
        dto.email(),
        dto.phone(),
        dto.taxId(),
        dto.address(),
        dto.city(),
        dto.country(),
        null,
        null,
        null,
        null
    );
  }

  public SupplierResponseDto toDto(Supplier domain) {
    return new SupplierResponseDto(
        domain.getId(),
        domain.getCommercialName(),
        domain.getContactName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getTaxId(),
        domain.getAddress(),
        domain.getCity(),
        domain.getCountry(),
        productDtoMapper.toSupplierDto(domain.getProducts()),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }
}
