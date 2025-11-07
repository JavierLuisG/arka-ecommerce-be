package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IDocumentAdapterPort;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.infrastructure.persistence.entity.DocumentEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.DocumentMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaDocumentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentPersistenceAdapter implements IDocumentAdapterPort {
  private final IJpaDocumentRepository jpaDocumentRepository;
  private final DocumentMapper mapper;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Document saveDocument(Document document) {
    DocumentEntity entity = mapper.toEntity(document);
    DocumentEntity saved = jpaDocumentRepository.save(entity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Document> findDocumentById(UUID id) {
    return jpaDocumentRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Document> findDocumentByNumber(String number) {
    return jpaDocumentRepository.findByNumber(number).map(mapper::toDomain);
  }

  @Override
  public List<Document> findAllDocuments() {
    return jpaDocumentRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Document> findAllDocumentsByStatus(DocumentStatus status) {
    return jpaDocumentRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsDocumentByNumber(String number) {
    return jpaDocumentRepository.existsByNumber(number);
  }
}
