package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.CreateProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.ModifyStockRequestDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateProductCategoriesDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateFieldsProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.CheckProductResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.ProductDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/products")
public class ProductController {
  public final IProductUseCase productUseCase;
  public final ProductDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping
  public ResponseEntity<ProductResponseDto> postProduct(@RequestBody @Valid CreateProductDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(productUseCase.createProduct(mapper.toDomain(dto))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductById(uuid)));
  }

  @GetMapping("/sku/{sku}")
  public ResponseEntity<ProductResponseDto> getProductBySku(@PathVariable("sku") String sku) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductBySku(sku)));
  }

  @GetMapping
  public ResponseEntity<List<ProductResponseDto>> getAllProducts(
      @RequestParam(required = false) String status
  ) {
    if (status == null) {
      return ResponseEntity.ok(productUseCase.getAllProducts().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    ProductStatus statusEnum = PathUtils.validateEnumOrThrow(ProductStatus.class, status, "ProductStatus");
    return ResponseEntity.ok(productUseCase.getAllProductsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDto> updateFieldsProduct(
      @PathVariable("id") String id,
      @RequestBody UpdateFieldsProductDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateFieldsProduct(uuid, mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/categories")
  public ResponseEntity<ProductResponseDto> updateCategoriesToProduct(
      @PathVariable("id") String id,
      @RequestBody UpdateProductCategoriesDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateCategories(uuid, dto.categories())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/decrease-stock")
  public ResponseEntity<MessageResponseDto> decreaseStockProduct(
      @PathVariable("id") String id,
      @RequestBody @Valid ModifyStockRequestDto decrease) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.decreaseStock(uuid, decrease.quantity());
    return ResponseEntity.ok(new MessageResponseDto(
        "Successful decrease " + decrease.quantity() + " for product ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/increase-stock")
  public ResponseEntity<MessageResponseDto> increaseStockProduct(
      @PathVariable("id") String id,
      @RequestBody @Valid ModifyStockRequestDto increase) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.increaseStock(uuid, increase.quantity());
    return ResponseEntity.ok(new MessageResponseDto(
        "Successful increase " + increase.quantity() + " for product ID " + id));
  }

  @GetMapping("/{id}/availability")
  public ResponseEntity<CheckProductResponseDto> checkAvailability(
      @PathVariable("id") String id,
      @RequestParam("quantity") @Min(1) int quantity) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    Product product = productUseCase.getProductById(uuid);
    return ResponseEntity.ok(new CheckProductResponseDto(
        product.isAvailableByStock(quantity),
        quantity,
        product.getStock()
    ));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteProduct(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.softDeleteProduct(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Product deleted successfully"));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<ProductResponseDto> restoreProduct(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.restoreProduct(uuid)));
  }
}
