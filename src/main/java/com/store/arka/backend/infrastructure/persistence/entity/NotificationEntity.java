package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.NotificationStatus;
import com.store.arka.backend.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "notifications", uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "type"}))
public class NotificationEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_customer"))
  @ToString.Exclude
  private CustomerEntity customer;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_order"))
  @ToString.Exclude
  private OrderEntity order;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType type;
  @Column(nullable = false, length = 500)
  private String message;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @Column(name = "read_at")
  private LocalDateTime readAt;

  @PrePersist
  public void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
