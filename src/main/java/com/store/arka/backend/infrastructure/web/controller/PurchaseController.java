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
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PostMapping
  public ResponseEntity<PurchaseResponseDto> postPurchase(@RequestBody @Valid CreatePurchaseDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(purchaseUseCase.createPurchase(mapper.toDomain(dto), dto.supplierId())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.getPurchaseById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(purchaseUseCase.getAllPurchases().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    PurchaseStatus statusEnum = PathUtils.validateEnumOrThrow(PurchaseStatus.class, status, "PurchaseStatus");
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/supplier/{supplierId}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesBySupplierId(@PathVariable("supplierId") String supplierId) {
    UUID supplierUuid = PathUtils.validateAndParseUUID(supplierId);
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesBySupplierId(supplierUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES', 'MANAGER')")
  @GetMapping("/items/product/{productId}")
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchasesByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(purchaseUseCase.getAllPurchasesByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/product/{productId}/add-item")
  public ResponseEntity<PurchaseResponseDto> addPurchaseItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToPurchaseItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.addPurchaseItem(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/product/{productId}/update-item-quantity")
  public ResponseEntity<PurchaseResponseDto> updatePurchaseItemQuantityById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToPurchaseItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.updatePurchaseItemQuantity(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/product/{productId}/remove-item")
  public ResponseEntity<PurchaseResponseDto> removePurchaseItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(purchaseUseCase.removePurchaseItem(uuid, productUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/confirm")
  public ResponseEntity<MessageResponseDto> confirmPurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.confirmPurchase(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully confirmed with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/receive")
  public ResponseEntity<MessageResponseDto> receivePurchaseById(
      @PathVariable("id") String id,
      @RequestBody @Valid ReceivePurchaseDto request) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.receivePurchase(uuid, mapper.toDomain(request));
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully received with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @PutMapping("/{id}/close")
  public ResponseEntity<MessageResponseDto> closePurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.closePurchase(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully closed with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASES')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> deletePurchaseById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    purchaseUseCase.deletePurchase(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Purchase has been successfully deleted with ID " + id));
  }
}
