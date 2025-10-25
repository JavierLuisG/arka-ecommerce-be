package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.OrderStatus;
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
@Table(name = "orders")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @Column(name = "cart_id", nullable = false, updatable = false)
  private UUID cartId;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_customer"))
  @ToString.Exclude
  private CustomerEntity customer;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<OrderItemEntity> items;
  @Column(nullable = false)
  private BigDecimal total;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
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
