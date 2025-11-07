package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.IDocumentAdapterPort;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService implements IDocumentUseCase {
  private final IDocumentAdapterPort documentAdapterPort;

  @Override
  public Document createDocument(Document document) {
    ValidateAttributesUtils.throwIfModelNull(document, "Document");
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(document.getNumber(), "Document number");
    if (documentAdapterPort.existsDocumentByNumber(normalizedNumber)) {
      log.warn("Document number '{}' already exists", normalizedNumber);
      throw new FieldAlreadyExistsException("Document number " + normalizedNumber + " already exists. Choose a different");
    }
    DocumentType type = PathUtils.validateEnumOrThrow(DocumentType.class, document.getType().toString(), "DocumentType");
    Document created = Document.create(type, normalizedNumber);
    Document saved = documentAdapterPort.saveDocument(created);
    log.info("Created new document {}, (ID: {}))", saved.getNumber(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Document getDocumentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return documentAdapterPort.findDocumentById(id)
        .orElseThrow(() -> {
          log.warn("Document with ID {} not found", id);
          return new ModelNotFoundException("Document with ID " + id + " not found");
        });
  }

  @Override
  @Transactional
  public Document getDocumentByNumber(String number) {
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Number");
    return documentAdapterPort.findDocumentByNumber(normalizedNumber)
        .orElseThrow(() -> {
          log.warn("Document with number {} not found", normalizedNumber);
          return new ModelNotFoundException("Document with number " + normalizedNumber + " not found");
        });
  }

  @Override
  @Transactional
  public List<Document> getAllDocuments() {
    log.info("Fetching all documents");
    return documentAdapterPort.findAllDocuments();
  }

  @Override
  @Transactional
  public List<Document> getAllDocumentsByStatus(DocumentStatus status) {
    log.info("Fetching all documents with status {}", status);
    return documentAdapterPort.findAllDocumentsByStatus(status);
  }

  @Override
  @Transactional
  public Document updateDocument(UUID id, Document document) {
    ValidateAttributesUtils.throwIfModelNull(document, "Document");
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(document.getNumber(), "Document number");
    Document found = getDocumentById(id);
    if (documentAdapterPort.existsDocumentByNumber(normalizedNumber) && !found.getNumber().equals(normalizedNumber)) {
      throw new FieldAlreadyExistsException("Document number already exists. Choose a different");
    }
    DocumentType type = PathUtils.validateEnumOrThrow(DocumentType.class, document.getType().toString(), "DocumentType");
    found.throwIfDeleted();
    found.update(type, normalizedNumber);
    Document saved = documentAdapterPort.saveDocument(found);
    log.info("Updated document ID {} ", saved.getId());
    return saved;
  }

  @Override
  public void softDeleteDocument(UUID id) {
    Document found = getDocumentById(id);
    found.delete();
    documentAdapterPort.saveDocument(found);
    log.info("Document ID {} marked as deleted", id);
  }

  @Override
  public Document restoreDocument(UUID id) {
    Document found = getDocumentById(id);
    found.restore();
    Document restored = documentAdapterPort.saveDocument(found);
    log.info("Document ID {} restored successfully", id);
    return restored;
  }
}
