package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.model.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISupplierAdapterPort {
  Supplier saveCreateSupplier(Supplier supplier);

  Supplier saveUpdateSupplier(Supplier supplier);

  Optional<Supplier> findSupplierById(UUID id);

  Optional<Supplier> findSupplierByEmail(String email);

  Optional<Supplier> findSupplierByTaxId(String taxId);

  List<Supplier> findAllSuppliers();

  List<Supplier> findAllSuppliersByStatus(SupplierStatus status);

  List<Supplier> findAllSuppliersByProductId(UUID productId);

  boolean existsSupplierByEmail(String email);

  boolean existsSupplierByTaxId(String taxId);
}
