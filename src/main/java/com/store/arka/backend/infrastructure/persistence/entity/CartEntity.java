package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.CartStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_customer"))
  @ToString.Exclude
  private CustomerEntity customer;
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<CartItemEntity> items = new ArrayList<>();
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CartStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
  @Column(name = "abandoned_at")
  private LocalDateTime abandonedAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
