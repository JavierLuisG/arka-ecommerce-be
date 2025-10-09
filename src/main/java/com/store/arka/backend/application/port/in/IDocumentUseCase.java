package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.model.Document;

import java.util.List;
import java.util.UUID;

public interface IDocumentUseCase {
  Document createDocument(Document document);

  Document getDocumentById(UUID id);

  Document getDocumentByIdAndStatus(UUID id, DocumentStatus status);

  Document getDocumentByNumber(String number);

  Document getDocumentByNumberAndStatus(String number, DocumentStatus status);

  List<Document> getAllDocuments();

  List<Document> getAllDocumentsByStatus(DocumentStatus status);

  Document updateDocument(UUID id, Document document);

  void deleteDocument(UUID id);

  Document restoreDocumentByNumber(String number);
}
