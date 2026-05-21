package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.infrastructure.web.dto.supplier.request.SupplierDto;
import com.store.arka.backend.infrastructure.web.dto.supplier.response.SupplierResponseDto;
import com.store.arka.backend.infrastructure.web.dto.supplier.response.SupplierResponseToPurchaseDto;
import com.store.arka.backend.shared.util.NormalizationUtils;
import com.store.arka.backend.shared.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupplierDtoMapper {
  private final ProductDtoMapper productDtoMapper;

  public Supplier toDomain(SupplierDto dto) {
    return new Supplier(
        null,
        NormalizationUtils.normalizeShortText(dto.commercialName()),
        NormalizationUtils.normalizeShortText(dto.contactName()),
        NormalizationUtils.normalizeEmail(dto.email()),
        NormalizationUtils.normalizePhone(dto.phone()),
        NormalizationUtils.normalizeIdentifier(dto.taxId()),
        NormalizationUtils.normalizeShortText(dto.address()),
        NormalizationUtils.normalizeShortText(dto.city()),
        PathUtils.validateEnumOrThrow(Country.class, dto.country(), "Country"),
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

  public SupplierResponseToPurchaseDto toPurchaseDto(Supplier domain) {
    return new SupplierResponseToPurchaseDto(
        domain.getId(),
        domain.getCommercialName(),
        domain.getContactName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getTaxId(),
        domain.getAddress(),
        domain.getCity(),
        domain.getCountry()
    );
  }
}
