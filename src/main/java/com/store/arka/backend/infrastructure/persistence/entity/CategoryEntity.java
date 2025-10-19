package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoryEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  private UUID id;
  @Column(unique = true, nullable = false)
  private String name;
  private String description;
  @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<ProductEntity> products = new HashSet<>();
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CategoryStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
