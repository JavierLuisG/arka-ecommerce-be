package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.domain.model.PurchaseItem;
import com.store.arka.backend.domain.model.Supplier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchases")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "supplier_id", nullable = false, foreignKey = @ForeignKey(name = "fk_purchase_supplier"))
  @ToString.Exclude
  private SupplierEntity supplier;
  @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<PurchaseItemEntity> items;
  @Column(nullable = false)
  private BigDecimal total;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PurchaseStatus status;
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
