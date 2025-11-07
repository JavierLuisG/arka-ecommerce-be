package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.model.Document;

import java.util.List;
import java.util.UUID;

public interface IDocumentUseCase {
  Document createDocument(Document document);

  Document getDocumentById(UUID id);

  Document getDocumentByNumber(String number);

  List<Document> getAllDocuments();

  List<Document> getAllDocumentsByStatus(DocumentStatus status);

  Document updateDocument(UUID id, Document document);

  void softDeleteDocument(UUID id);

  Document restoreDocument(UUID id);
}
