package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase_items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseItemEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_purchaseitem_product"))
  @ToString.Exclude
  private ProductEntity product;
  @Min(1)
  @Column(nullable = false)
  private Integer quantity;
  @Column(name = "unit_cost", nullable = false)
  private BigDecimal unitCost;
  @Column(nullable = false)
  private BigDecimal subtotal;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "purchase_id", nullable = false, foreignKey = @ForeignKey(name = "fk_purchaseitem_purchase"))
  @ToString.Exclude
  private PurchaseEntity purchase;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
