package com.store.arka.backend.infrastructure.persistence.repository;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IJpaDocumentRepository extends JpaRepository<DocumentEntity, UUID> {
  Optional<DocumentEntity> findByIdAndStatus(UUID id, DocumentStatus status);

  Optional<DocumentEntity> findByNumber(String number);

  Optional<DocumentEntity> findByNumberAndStatus(String number, DocumentStatus status);

  List<DocumentEntity> findAllByStatus(DocumentStatus status);

  boolean existsByNumber(String number);
}
