package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Document {
  @EqualsAndHashCode.Include
  private final UUID id;
  private DocumentType type;
  private String number;
  private DocumentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Document create(DocumentType type, String number) {
    validateNotNullOrEmpty(type.toString(), "Document type");
    validateNotNullOrEmpty(number, "Document number");
    return new Document(
        null,
        type,
        number,
        DocumentStatus.ACTIVE,
        null,
        null
    );
  }

  public void update(DocumentType type, String number) {
    validateNotNullOrEmpty(type.toString(), "Document type");
    validateNotNullOrEmpty(number, "Document number");
    this.type = type;
    this.number = number;
  }

  private static void validateNotNullOrEmpty(String value, String field) {
    if (value == null || value.trim().isEmpty()) throw new InvalidArgumentException(field + " cannot be null or empty");
  }

  public boolean isActive() {
    return this.status.equals(DocumentStatus.ACTIVE);
  }

  public void delete() {
    if (isActive()) {
      this.status = DocumentStatus.ELIMINATED;
    } else {
      throw new ModelDeletionException("Document already deleted previously");
    }
  }

  public void restore() {
    if (this.status == DocumentStatus.ELIMINATED) {
      this.status = DocumentStatus.ACTIVE;
      this.updatedAt = LocalDateTime.now();
    } else {
      throw new ModelActivationException("Document already active previously");
    }
  }
}
