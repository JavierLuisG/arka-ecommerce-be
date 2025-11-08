package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.in.ISupplierUseCase;
import com.store.arka.backend.application.port.out.ISupplierAdapterPort;
import com.store.arka.backend.domain.enums.Country;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.shared.util.PathUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierUseCase {
  private final ISupplierAdapterPort supplierAdapterPort;
  private final IProductUseCase productUseCase;

  @Override
  @Transactional
  public Supplier createSupplier(Supplier supplier) {
    ValidateAttributesUtils.throwIfModelNull(supplier, "Supplier");
    String normalizedEmail = ValidateAttributesUtils.throwIfNullOrEmpty(supplier.getEmail(), "Email").toLowerCase();
    String normalizedTaxId = ValidateAttributesUtils.throwIfNullOrEmpty(supplier.getTaxId(), "Tax id");
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, supplier.getCountry().toString(), "Country");
    if (supplierAdapterPort.existsSupplierByEmail(normalizedEmail)) {
      log.warn("[SUPPLIER_SERVICE][CREATE] Email {} already exists for creating a supplier", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " always exists in Supplier");
    }
    if (supplierAdapterPort.existsSupplierByTaxId(normalizedTaxId)) {
      log.warn("[SUPPLIER_SERVICE][CREATE] TaxId {} already exists for creating a supplier", normalizedTaxId);
      throw new FieldAlreadyExistsException("TaxId " + normalizedTaxId + " always exists in Supplier");
    }
    Supplier created = Supplier.create(
        supplier.getCommercialName(),
        supplier.getContactName(),
        normalizedEmail,
        supplier.getPhone(),
        supplier.getTaxId(),
        supplier.getAddress(),
        supplier.getCity(),
        normalizedCountry
    );
    Supplier saved = supplierAdapterPort.saveCreateSupplier(created);
    log.info("[SUPPLIER_SERVICE][CREATE] Created new supplier ID: {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return supplierAdapterPort.findSupplierById(id)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_ID] Supplier with ID {} not found", id);
          return new ModelNotFoundException("Supplier with ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierByEmail(String email) {
    String normalizedEmail = ValidateAttributesUtils.throwIfNullOrEmpty(email, "Email in Supplier").toLowerCase();
    return supplierAdapterPort.findSupplierByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_EMAIL] Supplier with Email {} not found", normalizedEmail);
          return new ModelNotFoundException("Supplier with email " + normalizedEmail + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierByTaxId(String taxId) {
    String normalizedTaxId = ValidateAttributesUtils.throwIfNullOrEmpty(taxId, "Taxt Id in Supplier");
    return supplierAdapterPort.findSupplierByTaxId(normalizedTaxId)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_TAX] Supplier with TaxId {} not found", normalizedTaxId);
          return new ModelNotFoundException("Supplier with taxId " + normalizedTaxId + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliers() {
    log.info("[SUPPLIER_SERVICE][GET_ALL] Fetching all suppliers");
    return supplierAdapterPort.findAllSuppliers();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliersByStatus(SupplierStatus status) {
    log.info("[SUPPLIER_SERVICE][GET_ALL_BY_STATUS] Fetching all suppliers with status {}", status);
    return supplierAdapterPort.findAllSuppliersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliersByProductId(UUID productId) {
    Product productFound = findProductOrThrow(productId);
    log.info("[SUPPLIER_SERVICE][GET_ALL_BY_PRODUCT] Fetching all suppliers by product ID {}", productId);
    return supplierAdapterPort.findAllSuppliersByProductId(productFound.getId());
  }

  @Override
  @Transactional
  public Supplier updateFieldsSupplier(UUID id, Supplier supplier) {
    ValidateAttributesUtils.throwIfModelNull(supplier, "Supplier");
    Supplier found = getSupplierById(id);
    String normalizedEmail = ValidateAttributesUtils.throwIfNullOrEmpty(supplier.getEmail(), "Email").toLowerCase();
    String normalizedTaxId = ValidateAttributesUtils.throwIfNullOrEmpty(supplier.getTaxId(), "Tax id");
    Country normalizedCountry = PathUtils.validateEnumOrThrow(
        Country.class, supplier.getCountry().toString(), "Country");
    if (!normalizedEmail.equals(found.getEmail()) && supplierAdapterPort.existsSupplierByEmail(normalizedEmail)) {
      log.warn("[SUPPLIER_SERVICE][UPDATE] Email {} already exists for updating a supplier", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " always exists in Supplier");
    }
    if (!normalizedTaxId.equals(found.getTaxId()) && supplierAdapterPort.existsSupplierByTaxId(normalizedTaxId)) {
      log.warn("[SUPPLIER_SERVICE][UPDATE] TaxId {} already exists for updating a supplier", normalizedTaxId);
      throw new FieldAlreadyExistsException("Tax " + normalizedTaxId + " always exists in Supplier");
    }
    found.updateFields(
        supplier.getCommercialName(),
        supplier.getContactName(),
        normalizedEmail,
        supplier.getPhone(),
        supplier.getTaxId(),
        supplier.getAddress(),
        supplier.getCity(),
        normalizedCountry
    );
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][UPDATE] Updated fields supplier ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Supplier addProduct(UUID id, UUID productId) {
    Product productFound = findProductOrThrow(productId);
    Supplier supplierFound = getSupplierById(id);
    supplierFound.addProduct(productFound);
    log.info("[SUPPLIER_SERVICE][ADD_PRODUCT] Supplier ID {} has added the product ID {}", supplierFound.getId(), productId);
    return supplierAdapterPort.saveUpdateSupplier(supplierFound);
  }

  @Override
  @Transactional
  public Supplier removeProduct(UUID id, UUID productId) {
    Supplier supplierFound = getSupplierById(id);
    Product productFound = findProductOrThrow(productId);
    supplierFound.removeProduct(productFound);
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(supplierFound);
    log.info("[SUPPLIER_SERVICE][REMOVE_PRODUCT] Supplier ID {} has removed the product ID {}", supplierFound.getId(), productId);
    return saved;
  }

  @Override
  @Transactional
  public void deleteSupplier(UUID id) {
    Supplier found = getSupplierById(id);
    found.delete();
    supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][DELETE] Supplier ID {} marked as deleted", id);
  }

  @Transactional
  public Supplier restoreSupplier(UUID id) {
    Supplier found = getSupplierById(id);
    found.restore();
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][RESTORE] Supplier ID {} restored successfully", id);
    return saved;
  }

  private Product findProductOrThrow(UUID productId) {
    ValidateAttributesUtils.throwIfModelNull(productId, "ProductId in Supplier");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }
}
