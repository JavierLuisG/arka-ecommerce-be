package com.store.arka.backend.infrastructure.persistence.mapper;

import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
  public Document toDomain(DocumentEntity entity) {
    if (entity == null) return null;
    return new Document(
        entity.getId(),
        entity.getType(),
        entity.getNumber(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public DocumentEntity toEntity(Document domain) {
    if (domain == null) return null;
    return new DocumentEntity(
        domain.getId(),
        domain.getType(),
        domain.getNumber(),
        null,
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public DocumentEntity toReference(Document domain) {
    if (domain == null) return null;
    DocumentEntity entity = new DocumentEntity();
    entity.setId(domain.getId());
    return entity;
  }
}
