package com.store.arka.backend.infrastructure.persistence.entity;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "order_items", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"}))
public class OrderItemEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, unique = true, updatable = false)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_product"))
  @ToString.Exclude
  private ProductEntity product;
  @Min(1)
  @Column(nullable = false)
  private Integer quantity;
  @Column(name = "product_price", nullable = false, updatable = false)
  private BigDecimal productPrice;
  @Column(nullable = false)
  private BigDecimal subtotal;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_order"))
  @ToString.Exclude
  private OrderEntity order;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
