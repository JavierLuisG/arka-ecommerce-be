package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.model.Supplier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISupplierUseCase {
  Supplier createSupplier(Supplier supplier);

  Supplier getSupplierById(UUID id);

  Supplier getSupplierByIdAndStatus(UUID id, SupplierStatus status);

  Supplier getSupplierByEmail(String email);

  Supplier getSupplierByEmailAndStatus(String email, SupplierStatus status);

  Supplier getSupplierByTaxId(String taxId);

  Supplier getSupplierByTaxIdAndStatus(String taxId, SupplierStatus status);

  Supplier getSupplierByIdAndProductIdAndStatus(UUID id, UUID productId, SupplierStatus status);

  List<Supplier> getAllSuppliers();

  List<Supplier> getAllSuppliersByStatus(SupplierStatus status);

  List<Supplier> getAllSuppliersByProductIdAndStatus(UUID productId, SupplierStatus status);

  Supplier updateFieldsSupplier(UUID id, Supplier supplier);

  Supplier addProduct(UUID id, UUID productId);

  Supplier removeProduct(UUID id, UUID productId);

  void deleteSupplierById(UUID id);

  Supplier restoreSupplierByEmail(String email);
}
