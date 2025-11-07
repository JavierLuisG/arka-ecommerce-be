package com.store.arka.backend.domain.model;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelActivationException;
import com.store.arka.backend.domain.exception.ModelDeletionException;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
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
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static Document create(DocumentType type, String number) {
    ValidateAttributesUtils.throwIfModelNull(type, "Document type");
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Document number");
    return new Document(
        null,
        type,
        normalizedNumber,
        DocumentStatus.ACTIVE,
        null,
        null
    );
  }

  public void update(DocumentType type, String number) {
    throwIfDeleted();
    ValidateAttributesUtils.throwIfModelNull(type, "Document type");
    String normalizedNumber = ValidateAttributesUtils.throwIfNullOrEmpty(number, "Document number");
    this.type = type;
    this.number = normalizedNumber;
  }

  public void delete() {
    if (isDeleted()) throw new ModelDeletionException("Document is already marked as deleted");
    this.status = DocumentStatus.ELIMINATED;
  }

  public void restore() {
    if (isActive()) throw new ModelActivationException("Document is already active and cannot be restored again");
    this.status = DocumentStatus.ACTIVE;
  }

  public boolean isActive() {
    return this.status == DocumentStatus.ACTIVE;
  }

  public boolean isDeleted() {
    return this.status == DocumentStatus.ELIMINATED;
  }

  public void throwIfDeleted() {
    if (isDeleted()) throw new ModelDeletionException("Document deleted previously");
  }
}
