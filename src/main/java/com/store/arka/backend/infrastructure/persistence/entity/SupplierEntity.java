package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suppliers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SupplierEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;
  @Column(nullable = false, length = 100)
  private String commercialName;
  @Column(name = "contact_name", nullable = false, length = 100)
  private String contactName;
  @Email
  @Column(nullable = false, unique = true, length = 100)
  private String email;
  @Column(nullable = false, length = 10)
  private String phone;
  @Column(nullable = false, unique = true, length = 20)
  private String taxId;
  @Column(nullable = false, length = 150)
  private String address;
  @Column(nullable = false, length = 100)
  private String city;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Country country;
  @ManyToMany
  @JoinTable(
      name = "supplier_products",
      joinColumns = @JoinColumn(name = "supplier_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  @ToString.Exclude
  private List<ProductEntity> products = new ArrayList<>();
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SupplierStatus status;
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
