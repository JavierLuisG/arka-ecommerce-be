package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.IDocumentAdapterPort;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.security.SecurityUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
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
  private final SecurityUtils securityUtils;

  @Override
  public Document createDocument(Document document) {
    ValidateAttributesUtils.throwIfModelNull(document, "Document");
    validateNumberExistence(document.getNumber(), null);
    Document created = Document.create(document.getType(), document.getNumber());
    Document saved = documentAdapterPort.saveDocument(created);
    log.info("[DOCUMENT_SERVICE][CREATED] User(id={}) has created new Document(number={}), ID {}",
        securityUtils.getCurrentUserId(), saved.getNumber(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Document getDocumentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Document ID");
    return documentAdapterPort.findDocumentById(id)
        .orElseThrow(() -> {
          log.warn("[DOCUMENT_SERVICE][GET_BY_ID] Document(id={}) not found", id);
          return new ModelNotFoundException("Document ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Document getDocumentByNumber(String number) {
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Number");
    return documentAdapterPort.findDocumentByNumber(normalizedNumber)
        .orElseThrow(() -> {
          log.warn("[DOCUMENT_SERVICE][GET_BY_NUMBER] Document(number={}) not found", normalizedNumber);
          return new ModelNotFoundException("Document with number " + normalizedNumber + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Document> getAllDocuments() {
    log.info("[DOCUMENT_SERVICE][GET_ALL] Fetching all Documents");
    return documentAdapterPort.findAllDocuments();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Document> getAllDocumentsByStatus(DocumentStatus status) {
    log.info("[DOCUMENT_SERVICE][GET_ALL_BY_STATUS] Fetching all Documents with status=({})", status);
    return documentAdapterPort.findAllDocumentsByStatus(status);
  }

  @Override
  @Transactional
  public Document updateDocument(UUID id, Document document) {
    ValidateAttributesUtils.throwIfModelNull(document, "Document");
    Document found = getDocumentById(id);
    validateNumberExistence(document.getNumber(), found.getNumber());
    found.update(document.getType(), document.getNumber());
    Document saved = documentAdapterPort.saveDocument(found);
    log.info("[DOCUMENT_SERVICE][UPDATED] User(id={}) has updated Document(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  public void softDeleteDocument(UUID id) {
    Document found = getDocumentById(id);
    found.delete();
    documentAdapterPort.saveDocument(found);
    log.info("[DOCUMENT_SERVICE][DELETED] User(id={}) has marked as deleted Document(id={})",
        securityUtils.getCurrentUserId(), id);
  }

  @Override
  public Document restoreDocument(UUID id) {
    Document found = getDocumentById(id);
    found.restore();
    Document restored = documentAdapterPort.saveDocument(found);
    log.info("[DOCUMENT_SERVICE][RESTORED] User(id={}) has restored Document(id={}) successfully",
        securityUtils.getCurrentUserId(), id);
    return restored;
  }

  private void validateNumberExistence(String newNumber, String oldNumber) {
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(newNumber, "Document number");
    boolean exists = documentAdapterPort.existsDocumentByNumber(normalizedNumber);
    if (oldNumber == null && exists) {
      log.warn("[DOCUMENT_SERVICE][CREATED] Document(number={}) already exists",
          normalizedNumber);
      throw new FieldAlreadyExistsException("Document number " + normalizedNumber + " already exists. Choose a different");
    }
    if (exists && !oldNumber.equals(normalizedNumber)) {
      log.warn("[DOCUMENT_SERVICE][UPDATED] Document(number={}) already exists",
          normalizedNumber);
      throw new FieldAlreadyExistsException("Document number already exists. Choose a different");
    }
  }
}
