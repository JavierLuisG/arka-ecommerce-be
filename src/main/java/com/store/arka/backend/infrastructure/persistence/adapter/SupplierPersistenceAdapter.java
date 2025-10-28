package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.ISupplierAdapterPort;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.infrastructure.persistence.entity.SupplierEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.SupplierMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaSupplierRepository;
import com.store.arka.backend.infrastructure.persistence.updater.SupplierUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SupplierPersistenceAdapter implements ISupplierAdapterPort {
  private final IJpaSupplierRepository jpaSupplierRepository;
  private final SupplierMapper mapper;
  private final SupplierUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Supplier saveCreateSupplier(Supplier supplier) {
    SupplierEntity entity = mapper.toEntity(supplier);
    SupplierEntity saved = jpaSupplierRepository.save(entity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Supplier saveUpdateSupplier(Supplier supplier) {
    SupplierEntity entity = jpaSupplierRepository.findById(supplier.getId())
        .orElseThrow(() -> new ModelNotFoundException("Supplier with id " + supplier.getId() + " not found"));
    SupplierEntity updated = updater.merge(entity, supplier);
    SupplierEntity saved = jpaSupplierRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Supplier> findSupplierById(UUID id) {
    return jpaSupplierRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByIdAndStatus(UUID id, SupplierStatus status) {
    return jpaSupplierRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByEmail(String email) {
    return jpaSupplierRepository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByEmailAndStatus(String email, SupplierStatus status) {
    return jpaSupplierRepository.findByEmailAndStatus(email, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByTaxId(String taxId) {
    return jpaSupplierRepository.findByTaxId(taxId).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByTaxIdAndStatus(String taxId, SupplierStatus status) {
    return jpaSupplierRepository.findByTaxIdAndStatus(taxId, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Supplier> findSupplierByIdAndProductIdAndStatus(UUID id, UUID productId, SupplierStatus status) {
    return jpaSupplierRepository.findByIdAndProductsIdAndStatus(id, productId, status).map(mapper::toDomain);
  }

  @Override
  public List<Supplier> findAllSuppliers() {
    return jpaSupplierRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Supplier> findAllSuppliersByStatus(SupplierStatus status) {
    return jpaSupplierRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Supplier> findAllSuppliersByProductIdAndStatus(UUID productId, SupplierStatus status) {
    return jpaSupplierRepository.findAllByProductsIdAndStatus(productId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public boolean existsSupplierByEmail(String email) {
    return jpaSupplierRepository.existsByEmail(email);
  }

  @Override
  public boolean existsSupplierByTaxId(String taxId) {
    return jpaSupplierRepository.existsByTaxId(taxId);
  }
}
