package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.domain.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DocumentEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  UUID id;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  DocumentType type;
  @Column(nullable = false, unique = true)
  String number;
  @OneToOne(mappedBy = "document")
  CustomerEntity customer;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  DocumentStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  LocalDateTime updatedAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
