package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ISupplierUseCase;
import com.store.arka.backend.application.port.out.ISupplierAdapterPort;
import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.FieldAlreadyExistsException;
import com.store.arka.backend.domain.exception.InvalidArgumentException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierUseCase {
  private final ISupplierAdapterPort supplierAdapterPort;

  @Override
  public Supplier createSupplier(Supplier supplier) {
    if (supplier == null) throw new ModelNullException("Supplier cannot be null");
    String normalizedCommercialName = supplier.getCommercialName().trim().toLowerCase();
    String normalizedContactName = supplier.getContactName().trim().toLowerCase();
    String normalizedEmail = supplier.getEmail().trim().toLowerCase();
    String normalizedAddress = supplier.getAddress().trim().toLowerCase();
    String normalizedCity = supplier.getCity().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, supplier.getCountry().toString(), "Country");
    if (supplierAdapterPort.existsSupplierByEmail(normalizedEmail)) {
      throw new FieldAlreadyExistsException("Email " + supplier.getEmail() + " always exist in Supplier");
    }
    if (supplierAdapterPort.existsSupplierByTaxId(supplier.getTaxId())) {
      throw new FieldAlreadyExistsException("Tax " + supplier.getTaxId() + " always exist in Supplier");
    }
    Supplier created = Supplier.create(
        normalizedCommercialName,
        normalizedContactName,
        normalizedEmail,
        supplier.getPhone(),
        supplier.getTaxId(),
        normalizedAddress,
        normalizedCity,
        normalizedCountry
    );
    return supplierAdapterPort.saveSupplier(created);
  }

  @Override
  public Supplier getSupplierById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return supplierAdapterPort.findSupplierById(id)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with id " + id + " not found"));
  }

  @Override
  public Supplier getSupplierByIdAndStatus(UUID id, SupplierStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return supplierAdapterPort.findSupplierByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with id " + id + " and status " + status + " not found"));
  }

  @Override
  public Supplier getSupplierByEmail(String email) {
    if (email == null || email.isBlank()) throw new InvalidArgumentException("Email in supplier is required");
    return supplierAdapterPort.findSupplierByEmail(email)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with email " + email + " not found"));
  }

  @Override
  public Supplier getSupplierByEmailAndStatus(String email, SupplierStatus status) {
    if (email == null || email.isBlank()) throw new InvalidArgumentException("Email in supplier is required");
    return supplierAdapterPort.findSupplierByEmailAndStatus(email, status)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with email " + email + " and status " + status + " not found"));
  }

  @Override
  public Supplier getSupplierByTaxId(String taxId) {
    if (taxId == null || taxId.isBlank()) throw new InvalidArgumentException("Tax in supplier is required");
    return supplierAdapterPort.findSupplierByTaxId(taxId)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with taxId " + taxId + " not found"));
  }

  @Override
  public Supplier getSupplierByTaxIdAndStatus(String taxId, SupplierStatus status) {
    if (taxId == null || taxId.isBlank()) throw new InvalidArgumentException("Tax in supplier is required");
    return supplierAdapterPort.findSupplierByTaxIdAndStatus(taxId, status)
        .orElseThrow(() -> new ModelNotFoundException("Supplier with taxId " + taxId + " and status " + status + " not found"));
  }

  @Override
  public List<Supplier> getAllSuppliers() {
    return supplierAdapterPort.findAllSuppliers();
  }

  @Override
  public List<Supplier> getAllSuppliersByStatus(SupplierStatus status) {
    return supplierAdapterPort.findAllSuppliersByStatus(status);
  }

  @Override
  public Supplier updateFieldsSupplier(UUID id, Supplier supplier) {
    if (supplier == null) throw new ModelNullException("Supplier cannot be null");
    Supplier found = getSupplierByIdAndStatus(id, SupplierStatus.ACTIVE);
    String normalizedCommercialName = supplier.getCommercialName().trim().toLowerCase();
    String normalizedContactName = supplier.getContactName().trim().toLowerCase();
    String normalizedEmail = supplier.getEmail().trim().toLowerCase();
    String normalizedAddress = supplier.getAddress().trim().toLowerCase();
    String normalizedCity = supplier.getCity().trim().toLowerCase();
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, supplier.getCountry().toString(), "Country");
    if (!normalizedEmail.equals(found.getEmail()) && supplierAdapterPort.existsSupplierByEmail(normalizedEmail)) {
      throw new FieldAlreadyExistsException("Email " + supplier.getEmail() + " always exist in Supplier");
    }
    if (!supplier.getTaxId().equals(found.getTaxId()) && supplierAdapterPort.existsSupplierByTaxId(supplier.getTaxId())) {
      throw new FieldAlreadyExistsException("Tax " + supplier.getTaxId() + " always exist in Supplier");
    }
    found.updateFields(
        normalizedCommercialName,
        normalizedContactName,
        normalizedEmail,
        supplier.getPhone(),
        supplier.getTaxId(),
        normalizedAddress,
        normalizedCity,
        normalizedCountry
    );
    return supplierAdapterPort.saveSupplier(found);
  }

  @Override
  public void deleteSupplierById(UUID id) {
    Supplier found = getSupplierById(id);
    found.delete();
    supplierAdapterPort.saveSupplier(found);
  }

  @Override
  public Supplier restoreSupplierByEmail(String email) {
    Supplier found = getSupplierByEmail(email);
    found.restore();
    return supplierAdapterPort.saveSupplier(found);
  }
}
