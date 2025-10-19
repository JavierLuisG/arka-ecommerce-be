package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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
@Table(name = "products")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  private UUID id;
  @Version
  private Long version;
  @Column(unique = true, nullable = false)
  private String sku;
  @Column(nullable = false)
  private String name;
  private String description;
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "product_category",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  @ToString.Exclude
  private Set<CategoryEntity> categories = new HashSet<>();
  @Column(nullable = false)
  private Integer stock;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProductStatus status;
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
