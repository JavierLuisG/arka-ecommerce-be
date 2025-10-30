package com.store.arka.backend.infrastructure.web.dto.supplier.response;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToSupplierDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SupplierResponseToPurchaseDto(
    UUID id,
    String commercialName,
    String contactName,
    String email,
    String phone,
    String taxId,
    String address,
    String city,
    Country country
) {
}
