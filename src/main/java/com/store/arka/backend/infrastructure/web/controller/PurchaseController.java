package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IPurchaseUseCase;
import com.store.arka.backend.domain.enums.PurchaseStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.request.CreatePurchaseDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.request.ReceivePurchaseDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.request.UpdateQuantityToPurchaseItemDto;
import com.store.arka.backend.infrastructure.web.dto.purchase.response.PurchaseResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.PurchaseDtoMapper;
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
@RequestMapping("/api/purchases")
public class PurchaseController {
  private final IPurchaseUseCase purchaseUseCase;
  private final PurchaseDtoMapper mapper;

  @PostMapping
  public ResponseEntity<PurchaseResponseDto> postPurchase(@RequestBody @Valid CreatePurchaseDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(purchaseUseCase.createPurchase(mapper.toDomain(dto), dto.supplierId())));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.getPurchaseById(uuid)));
  }

  @GetMapping("/{id}/status/{status}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.getPurchaseByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/{id}/supplier/{supplierId}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseByIdAndSupplierId(
      @PathVariable("id") String id,
      @PathVariable("supplierId") String supplierId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.getPurchaseByIdAndSupplierId(uuid, supplierUuid)));
  }

  @GetMapping("/{id}/supplier/{supplierId}/status/{status}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseByIdAndSupplierIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("supplierId") String supplierId,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(
        mapper.toDto(purchaseUseCase.getPurchaseByIdAndSupplierIdAndStatus(uuid, supplierUuid, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases() {
    return ResponseEntity.ok(purchaseUseCase.getAllPurchases().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesByStatus(@PathVariable("status") String status) {
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/supplier/{supplierId}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesBySupplierId(@PathVariable("supplierId") String supplierId) {
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesBySupplierId(supplierUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/supplier/{supplierId}/status/{status}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesBySupplierIdAndStatus(
      @PathVariable("supplierId") String supplierId,
      @PathVariable("status") String status) {
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesBySupplierIdAndStatus(supplierUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/product/{productId}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/product/{productId}/status/{status}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesByItemsProductIdAndStatus(
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesByItemsProductIdAndStatus(productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/supplier/{supplierId}/items/product/{productId}/status/{status}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesBySupplierIdAndItemsProductIdAndStatus(
      @PathVariable("supplierId") String supplierId,
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(purchaseUseCase
        .getAllPurchasesBySupplierIdAndItemsProductIdAndStatus(supplierUuid, productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}/product/{productId}/add-item")
  public ResponseEntity<PurchaseResponseDto> addPurchaseItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToPurchaseItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.addPurchaseItemById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/{id}/product/{productId}/update-item-quantity")
  public ResponseEntity<PurchaseResponseDto> updatePurchaseItemQuantityById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToPurchaseItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.updatePurchaseItemQuantityById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/{id}/product/{productId}/remove-item")
  public ResponseEntity<PurchaseResponseDto> removePurchaseItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.removePurchaseItemById(uuid, productUuid)));
  }

  @PutMapping("/{id}/confirm")
  public ResponseEntity<MessageResponseDto> confirmPurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.confirmPurchaseById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully confirmed with id: " + id));
  }

  @PutMapping("/{id}/receive")
  public ResponseEntity<MessageResponseDto> receivePurchaseById(
      @PathVariable("id") String id,
      @RequestBody @Valid ReceivePurchaseDto request) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.receivePurchaseById(uuid, mapper.toDomain(request));
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully received with id: " + id));
  }

  @PutMapping("/{id}/close")
  public ResponseEntity<MessageResponseDto> closePurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.closePurchaseById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully closed with id: " + id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> deletePurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.deletePurchaseById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully deleted with id: " + id));
  }
}
