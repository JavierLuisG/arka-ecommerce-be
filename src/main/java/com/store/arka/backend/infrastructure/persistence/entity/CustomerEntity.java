package com.store.arka.backend.infrastructure.persistence.entity;

import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.CustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CustomerEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  UUID id;
  @OneToOne
  @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "fk_document"))
  DocumentEntity document;
  @Column(name = "first_name", nullable = false)
  String firstName;
  @Column(name = "last_name", nullable = false)
  String lastName;
  @Email
  @Column(nullable = false, unique = true)
  String email;
  @Column(nullable = false)
  String phone;
  @Column(nullable = false)
  String address;
  @Column(nullable = false)
  String city;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  Country country;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  CustomerStatus status;
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  LocalDateTime createdAt;
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  LocalDateTime updatedAt;

  @PrePersist
  private void prePersist() {
    if (id == null) id = UUID.randomUUID();
  }
}
