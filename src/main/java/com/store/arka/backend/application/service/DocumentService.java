package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.application.port.out.IDocumentAdapterPort;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.Document;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService implements IDocumentUseCase {
  private final IDocumentAdapterPort documentAdapterPort;

  @Override
  public Document createDocument(Document document) {
    if (document == null) throw new ModelNullException("Document cannot be null");
    if (documentAdapterPort.existsDocumentByNumber(document.getNumber())) {
      throw new FieldAlreadyExistsException("Document number already exist");
    }
    Document created = Document.create(document.getType(), document.getNumber());
    return documentAdapterPort.saveDocument(created);
  }

  @Override
  public Document getDocumentById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return documentAdapterPort.findDocumentById(id)
        .orElseThrow(() -> new ModelNotFoundException("Document with id " + id + " not found"));
  }

  @Override
  public Document getDocumentByIdAndStatus(UUID id, DocumentStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return documentAdapterPort.findDocumentByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Document with id " + id + " and status " + status + " not found"));
  }

  @Override
  public Document getDocumentByNumber(String number) {
    if (number == null || number.isBlank()) throw new InvalidArgumentException("Number is required");
    return documentAdapterPort.findDocumentByNumber(number)
        .orElseThrow(() -> new ModelNotFoundException("Document with number " + number + " not found"));
  }

  @Override
  public Document getDocumentByNumberAndStatus(String number, DocumentStatus status) {
    if (number == null || number.isBlank()) throw new InvalidArgumentException("Number is required");
    return documentAdapterPort.findDocumentByNumberAndStatus(number, status)
        .orElseThrow(() -> new ModelNotFoundException("Document with number " + number + " and status " + status + " not found"));
  }

  @Override
  public List<Document> getAllDocuments() {
    return documentAdapterPort.findAllDocuments();
  }

  @Override
  public List<Document> getAllDocumentsByStatus(DocumentStatus status) {
    return documentAdapterPort.findAllDocumentsByStatus(status);
  }

  @Override
  public Document updateDocument(UUID id, Document document) {
    if (document == null) throw new ModelNullException("Document cannot be null");
    DocumentType normalizedType = PathUtils.validateEnumOrThrow(
        DocumentType.class, document.getType().toString(), "DocumentType");
    Document found = getDocumentByIdAndStatus(id, DocumentStatus.ACTIVE);
    if (documentAdapterPort.existsDocumentByNumber(document.getNumber())
        && !found.getNumber().equals(document.getNumber())) {
      throw new FieldAlreadyExistsException("Document number already exist");
    }
    found.update(normalizedType, document.getNumber());
    return documentAdapterPort.saveDocument(found);
  }

  @Override
  public void deleteDocument(UUID id) {
    Document found = getDocumentByIdAndStatus(id, DocumentStatus.ACTIVE);
    found.delete();
    documentAdapterPort.saveDocument(found);
  }

  @Override
  public Document restoreDocumentByNumber(String number) {
    Document found = getDocumentByNumberAndStatus(number, DocumentStatus.ELIMINATED);
    found.restore();
    return documentAdapterPort.saveDocument(found);
  }
}
