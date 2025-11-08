package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ISupplierUseCase;
import com.store.arka.backend.domain.enums.SupplierStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.supplier.request.SupplierDto;
import com.store.arka.backend.infrastructure.web.dto.supplier.response.SupplierResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.SupplierDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {
  private final ISupplierUseCase supplierUseCase;
  private final SupplierDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PostMapping
  public ResponseEntity<SupplierResponseDto> postSupplier(@RequestBody @Valid SupplierDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(supplierUseCase.createSupplier(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/email/{email}")
  public ResponseEntity<SupplierResponseDto> getSupplierByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByEmail(email)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/tax-id/{taxId}")
  public ResponseEntity<SupplierResponseDto> getSupplierByTaxId(@PathVariable("taxId") String taxId) {
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByTaxId(taxId)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers(
      @RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(supplierUseCase.getAllSuppliers().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(supplierUseCase.getAllSuppliersByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/product/{productId}")
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliersByProductId(
      @PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(supplierUseCase.getAllSuppliersByProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> updateSupplier(
      @PathVariable("id") String id,
      @RequestBody @Valid SupplierDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.updateFieldsSupplier(uuid, mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/product/{productId}/add")
  public ResponseEntity<SupplierResponseDto> addProduct(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.addProduct(uuid, productUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/product/{productId}/remove")
  public ResponseEntity<SupplierResponseDto> removeProduct(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.removeProduct(uuid, productUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteSupplier(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    supplierUseCase.deleteSupplier(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Supplier with id " + id + " eliminated successfully"));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<SupplierResponseDto> restoreSupplier(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.restoreSupplier(uuid)));
  }
}
