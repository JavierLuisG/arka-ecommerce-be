package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @Version
  private Long version;
  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_payment_order"))
  private OrderEntity order;
  @Column(nullable = false)
  private BigDecimal amount;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentMethod method;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;
  @Max(3)
  @Column(name = "failed_attempts", nullable = false)
  private Integer failedAttempts = 0;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
  @Column(name = "processed_at")
  private LocalDateTime processedAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
