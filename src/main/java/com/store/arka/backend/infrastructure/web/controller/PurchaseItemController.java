package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IPurchaseItemUseCase;
import com.store.arka.backend.infrastructure.web.dto.purchase.response.PurchaseItemResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.PurchaseItemDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-items")
public class PurchaseItemController {
  private final IPurchaseItemUseCase purchaseItemUseCase;
  private final PurchaseItemDtoMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<PurchaseItemResponseDto> getPurchaseItemById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(purchaseItemUseCase.getPurchaseItemById(uuid)));
  }

  @GetMapping
  public ResponseEntity<List<PurchaseItemResponseDto>> getAllPurchaseItems() {
    return ResponseEntity.ok(purchaseItemUseCase.getAllPurchaseItems()
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<PurchaseItemResponseDto>> getAllPurchaseItemsByProductId(
      @PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(purchaseItemUseCase.getAllPurchaseItemsByProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }
}
