package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.model.Supplier;

import java.util.List;
import java.util.UUID;

public interface ISupplierUseCase {
  Supplier createSupplier(Supplier supplier);

  Supplier getSupplierById(UUID id);

  Supplier getSupplierByEmail(String email);

  Supplier getSupplierByTaxId(String taxId);

  List<Supplier> getAllSuppliers();

  List<Supplier> getAllSuppliersByStatus(SupplierStatus status);

  List<Supplier> getAllSuppliersByProductId(UUID productId);

  Supplier updateFieldsSupplier(UUID id, Supplier supplier);

  Supplier addProduct(UUID id, UUID productId);

  Supplier removeProduct(UUID id, UUID productId);

  void deleteSupplier(UUID id);

  Supplier restoreSupplier(UUID id);
}
