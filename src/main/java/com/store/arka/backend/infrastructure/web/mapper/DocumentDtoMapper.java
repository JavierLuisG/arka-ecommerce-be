package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.enums.DocumentType;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.infrastructure.web.dto.customer.request.DocumentRequestDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.DocumentResponseDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.DocumentResponseToCustomerDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentDtoMapper {
  public Document toDomain(DocumentRequestDto dto) {
    return new Document(
        null,
        DocumentType.valueOf(dto.type()),
        dto.number(),
        null,
        null,
        null
    );
  }

  public DocumentResponseDto toDto(Document domain) {
    return new DocumentResponseDto(
        domain.getId(),
        domain.getType(),
        domain.getNumber(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public DocumentResponseToCustomerDto toCustomerDto(Document domain) {
    return new DocumentResponseToCustomerDto(
        domain.getId(),
        domain.getType(),
        domain.getNumber()
    );
  }
}