package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.in.ISupplierUseCase;
import com.store.arka.backend.application.port.out.ISupplierAdapterPort;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.domain.model.Supplier;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Supplier createSupplier(Supplier supplier) {
    ValidateAttributesUtils.throwIfModelNull(supplier, "Supplier");
    validateEmailExistence(supplier.getEmail(), null);
    validateTaxIdExistence(supplier.getTaxId(), null);
    Supplier created = Supplier.create(
        supplier.getCommercialName(),
        supplier.getContactName(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getTaxId(),
        supplier.getAddress(),
        supplier.getCity(),
        supplier.getCountry()
    );
    Supplier saved = supplierAdapterPort.saveCreateSupplier(created);
    log.info("[SUPPLIER_SERVICE][CREATED] User(id={}) has created new Supplier(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Supplier ID");
    return supplierAdapterPort.findSupplierById(id)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_ID] Supplier(id={}) not found", id);
          return new ModelNotFoundException("Supplier ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierByEmail(String email) {
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(email, "Email in Supplier");
    return supplierAdapterPort.findSupplierByEmail(normalizedEmail)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_EMAIL] Supplier(email={}) not found", normalizedEmail);
          return new ModelNotFoundException("Supplier with email " + normalizedEmail + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Supplier getSupplierByTaxId(String taxId) {
    String normalizedTaxId = ValidateAttributesUtils.throwIfValueNotAllowed(taxId, "TaxId in Supplier");
    return supplierAdapterPort.findSupplierByTaxId(normalizedTaxId)
        .orElseThrow(() -> {
          log.warn("[SUPPLIER_SERVICE][GET_BY_TAX] Supplier(taxId={}) not found", normalizedTaxId);
          return new ModelNotFoundException("Supplier with taxId " + normalizedTaxId + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliers() {
    log.info("[SUPPLIER_SERVICE][GET_ALL] Fetching all Suppliers");
    return supplierAdapterPort.findAllSuppliers();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliersByStatus(SupplierStatus status) {
    log.info("[SUPPLIER_SERVICE][GET_ALL_BY_STATUS] Fetching all Suppliers with status=({})", status);
    return supplierAdapterPort.findAllSuppliersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Supplier> getAllSuppliersByProductId(UUID productId) {
    Product productFound = findProductOrThrow(productId);
    log.info("[SUPPLIER_SERVICE][GET_ALL_BY_PRODUCT] Fetching all Suppliers by Product(id={})", productId);
    return supplierAdapterPort.findAllSuppliersByProductId(productFound.getId());
  }

  @Override
  @Transactional
  public Supplier updateFieldsSupplier(UUID id, Supplier supplier) {
    ValidateAttributesUtils.throwIfModelNull(supplier, "Supplier");
    Supplier found = getSupplierById(id);
    validateEmailExistence(supplier.getEmail(), found.getEmail());
    validateTaxIdExistence(supplier.getTaxId(), found.getTaxId());
    found.updateFields(
        supplier.getCommercialName(),
        supplier.getContactName(),
        supplier.getEmail(),
        supplier.getPhone(),
        supplier.getTaxId(),
        supplier.getAddress(),
        supplier.getCity(),
        supplier.getCountry()
    );
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][UPDATED] User(id={}) has updated fields Supplier(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Supplier addProduct(UUID id, UUID productId) {
    Product productFound = findProductOrThrow(productId);
    Supplier supplierFound = getSupplierById(id);
    supplierFound.addProduct(productFound);
    log.info("[SUPPLIER_SERVICE][ADDED_PRODUCT] User(id={}) has added the Product(id={}) in Supplier(id={})",
        securityUtils.getCurrentUserId(), productId, supplierFound.getId());
    return supplierAdapterPort.saveUpdateSupplier(supplierFound);
  }

  @Override
  @Transactional
  public Supplier removeProduct(UUID id, UUID productId) {
    Supplier supplierFound = getSupplierById(id);
    Product productFound = findProductOrThrow(productId);
    supplierFound.removeProduct(productFound);
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(supplierFound);
    log.info("[SUPPLIER_SERVICE][REMOVED_PRODUCT] User(id={}) has removed the Product(id={}) in Supplier(id={})",
        securityUtils.getCurrentUserId(), productId, supplierFound.getId());
    return saved;
  }

  @Override
  @Transactional
  public void deleteSupplier(UUID id) {
    Supplier found = getSupplierById(id);
    found.delete();
    supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][DELETED] User(id={}) has marked as deleted Supplier(id={})",
        securityUtils.getCurrentUserId(), found.getId());
  }

  @Override
  @Transactional
  public Supplier restoreSupplier(UUID id) {
    Supplier found = getSupplierById(id);
    found.restore();
    Supplier saved = supplierAdapterPort.saveUpdateSupplier(found);
    log.info("[SUPPLIER_SERVICE][RESTORED] User(id={}) has restored Supplier(id={}) successfully",
        securityUtils.getCurrentUserId(), id);
    return saved;
  }

  private Product findProductOrThrow(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Supplier");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private void validateTaxIdExistence(String newTaxId, String oldTaxId) {
    String normalizedTaxId = ValidateAttributesUtils.throwIfValueNotAllowed(newTaxId, "TaxId");
    boolean exists = supplierAdapterPort.existsSupplierByTaxId(normalizedTaxId);
    if (exists && oldTaxId == null) {
      log.warn("[SUPPLIER_SERVICE][CREATED] TaxId={} already exists for creating a Supplier", normalizedTaxId);
      throw new FieldAlreadyExistsException("TaxId " + normalizedTaxId + " already exists in Supplier");
    }
    if (exists && !oldTaxId.equals(normalizedTaxId)) {
      log.warn("[SUPPLIER_SERVICE][UPDATED] TaxId={} already exists for updating a Supplier", normalizedTaxId);
      throw new FieldAlreadyExistsException("TaxId " + normalizedTaxId + " already exists in Supplier");
    }
  }

  private void validateEmailExistence(String newEmail, String oldEmail) {
    String normalizedEmail = ValidateAttributesUtils.throwIfValueNotAllowed(newEmail, "Email");
    boolean exists = supplierAdapterPort.existsSupplierByEmail(normalizedEmail);
    if (oldEmail == null && exists) {
      log.warn("[SUPPLIER_SERVICE][CREATED] Email={} already exists for creating a Supplier", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists in Supplier");
    }
    if (exists && !oldEmail.equals(normalizedEmail)) {
      log.warn("[SUPPLIER_SERVICE][UPDATED] Email={} already exists for updating a Supplier", normalizedEmail);
      throw new FieldAlreadyExistsException("Email " + normalizedEmail + " already exists in Supplier");
    }
  }
}
