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

  @PostMapping
  public ResponseEntity<SupplierResponseDto> postSupplier(@RequestBody @Valid SupplierDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(supplierUseCase.createSupplier(mapper.toDomain(dto))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierById(uuid)));
  }

  @GetMapping("/{id}/status/{status}")
  public ResponseEntity<SupplierResponseDto> getSupplierByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<SupplierResponseDto> getSupplierByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByEmail(email)));
  }

  @GetMapping("/email/{email}/status/{status}")
  public ResponseEntity<SupplierResponseDto> getSupplierByEmailAndStatus(
      @PathVariable("email") String email,
      @PathVariable("status") String status) {
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByEmailAndStatus(email, statusEnum)));
  }

  @GetMapping("/tax-id/{taxId}")
  public ResponseEntity<SupplierResponseDto> getSupplierByTaxId(@PathVariable("taxId") String taxId) {
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByTaxId(taxId)));
  }

  @GetMapping("/tax-id/{taxId}/status/{status}")
  public ResponseEntity<SupplierResponseDto> getSupplierByTaxIdAndStatus(
      @PathVariable("taxId") String taxId,
      @PathVariable("status") String status) {
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.getSupplierByTaxIdAndStatus(taxId, statusEnum)));
  }

  @GetMapping("/{id}/product/{productId}/status/{status}")
  public ResponseEntity<SupplierResponseDto> getSupplierByIdAndProductIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(mapper.toDto(
        supplierUseCase.getSupplierByIdAndProductIdAndStatus(uuid, productUuid, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
    return ResponseEntity.ok(
        supplierUseCase.getAllSuppliers().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliersByStatus(@PathVariable("status") String status) {
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(
        supplierUseCase.getAllSuppliersByStatus(statusEnum).stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/product/{productId}/status/{status}")
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliersByProductIdAndStatus(
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    SupplierStatus statusEnum = PathUtils.validateEnumOrThrow(SupplierStatus.class, status, "SupplierStatus");
    return ResponseEntity.ok(supplierUseCase.getAllSuppliersByProductIdAndStatus(productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> putSupplierById(
      @PathVariable("id") String id,
      @RequestBody @Valid SupplierDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.updateFieldsSupplier(uuid, mapper.toDomain(dto))));
  }

  @PutMapping("/{id}/product/{productId}/add")
  public ResponseEntity<SupplierResponseDto> addProductById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.addProduct(uuid, productUuid)));
  }

  @PutMapping("/{id}/product/{productId}/remove")
  public ResponseEntity<SupplierResponseDto> removeProductById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.removeProduct(uuid, productUuid)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteSupplierById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    supplierUseCase.deleteSupplierById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Supplier with id " + id + " eliminated successfully"));
  }

  @PutMapping("/email/{email}/restore")
  public ResponseEntity<SupplierResponseDto> restoreSupplierByEmail(@PathVariable("email") String email) {
    return ResponseEntity.ok(mapper.toDto(supplierUseCase.restoreSupplierByEmail(email)));
  }
}
