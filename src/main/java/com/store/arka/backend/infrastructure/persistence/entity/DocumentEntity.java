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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DocumentEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  private UUID id;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DocumentType type;
  @Column(nullable = false, unique = true)
  private String number;
  @OneToOne(mappedBy = "document")
  @ToString.Exclude
  private CustomerEntity customer;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DocumentStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
