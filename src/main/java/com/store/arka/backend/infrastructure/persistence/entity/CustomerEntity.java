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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  private UUID id;
  @OneToOne
  @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "fk_document"))
  @ToString.Exclude
  private DocumentEntity document;
  @Column(name = "first_name", nullable = false)
  private String firstName;
  @Column(name = "last_name", nullable = false)
  private String lastName;
  @Email
  @Column(nullable = false, unique = true)
  private String email;
  @Column(nullable = false)
  private String phone;
  @Column(nullable = false)
  private String address;
  @Column(nullable = false)
  private String city;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Country country;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CustomerStatus status;
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
