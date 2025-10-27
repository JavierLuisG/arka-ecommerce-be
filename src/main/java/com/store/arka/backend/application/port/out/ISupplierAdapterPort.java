package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.model.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISupplierAdapterPort {
  Supplier saveSupplier(Supplier supplier);
//  Supplier saveCreateSupplier(Supplier supplier);

//  Supplier saveUpdateSupplier(Supplier supplier);

  Optional<Supplier> findSupplierById(UUID id);

  Optional<Supplier> findSupplierByIdAndStatus(UUID id, SupplierStatus status);

  Optional<Supplier> findSupplierByEmail(String email);

  Optional<Supplier> findSupplierByEmailAndStatus(String email, SupplierStatus status);

  Optional<Supplier> findSupplierByTaxId(String taxId);

  Optional<Supplier> findSupplierByTaxIdAndStatus(String taxId, SupplierStatus status);

  List<Supplier> findAllSuppliers();

  List<Supplier> findAllSuppliersByStatus(SupplierStatus status);

  boolean existsSupplierByEmail(String email);

  boolean existsSupplierByTaxId(String taxId);
}
