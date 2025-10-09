package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentAdapterPort {
  Document saveDocument(Document document);

  Optional<Document> findDocumentById(UUID id);

  Optional<Document> findDocumentByIdAndStatus(UUID id, DocumentStatus status);

  Optional<Document> findDocumentByNumber(String number);

  Optional<Document> findDocumentByNumberAndStatus(String number, DocumentStatus status);

  List<Document> findAllDocuments();

  List<Document> findAllDocumentsByStatus(DocumentStatus status);

  boolean existsDocumentByNumber(String number);
}
