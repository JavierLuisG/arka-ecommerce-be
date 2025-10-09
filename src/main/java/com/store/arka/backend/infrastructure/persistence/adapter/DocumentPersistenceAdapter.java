package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.IDocumentAdapterPort;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.infrastructure.persistence.mapper.DocumentMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaDocumentRepository;
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

  @Override
  public Document saveDocument(Document document) {
    return mapper.toDomain(jpaDocumentRepository.save(mapper.toEntity(document)));
  }

  @Override
  public Optional<Document> findDocumentById(UUID id) {
    return jpaDocumentRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Document> findDocumentByIdAndStatus(UUID id, DocumentStatus status) {
    return jpaDocumentRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Document> findDocumentByNumber(String number) {
    return jpaDocumentRepository.findByNumber(number).map(mapper::toDomain);
  }

  @Override
  public Optional<Document> findDocumentByNumberAndStatus(String number, DocumentStatus status) {
    return jpaDocumentRepository.findByNumberAndStatus(number, status).map(mapper::toDomain);
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
