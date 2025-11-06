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

  @GetMapping("/{id}/status/{status}")
  public ResponseEntity<ProductResponseDto> getProductByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    ProductStatus statusEnum = PathUtils.validateEnumOrThrow(ProductStatus.class, status, "ProductStatus");
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/sku/{sku}")
  public ResponseEntity<ProductResponseDto> getProductIsNotDeletedBySku(@PathVariable("sku") String sku) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductBySku(sku)));
  }

  @GetMapping("/sku/{sku}/status/{status}")
  public ResponseEntity<ProductResponseDto> getProductIsNotDeletedBySkuAndStatus(
      @PathVariable("sku") String sku,
      @PathVariable("status") String status) {
    ProductStatus statusEnum = PathUtils.validateEnumOrThrow(ProductStatus.class, status, "ProductStatus");
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductBySkuAndStatus(sku, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
    return ResponseEntity.ok(productUseCase.getAllProducts().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<ProductResponseDto>> getAllProductsByStatus(@PathVariable("status") String status) {
    ProductStatus statusEnum = PathUtils.validateEnumOrThrow(ProductStatus.class, status, "ProductStatus");
    return ResponseEntity.ok(productUseCase.getAllProductsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDto> putFieldsProductById(
      @PathVariable("id") String id,
      @RequestBody UpdateFieldsProductDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateFieldsProduct(uuid, mapper.toDomain(dto))));
  }

  @PutMapping("/{id}/categories")
  public ResponseEntity<ProductResponseDto> putCategoriesToProductById(
      @PathVariable("id") String id,
      @RequestBody UpdateProductCategoriesDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateCategories(uuid, dto.categories())));
  }

  @PutMapping("/{id}/decrease-stock")
  public ResponseEntity<MessageResponseDto> decreaseStockProductById(
      @PathVariable("id") String id,
      @RequestBody @Valid ModifyStockRequestDto decrease) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.decreaseStock(uuid, decrease.quantity());
    return ResponseEntity.ok(new MessageResponseDto("Successful decrease for product with id: " + id));
  }

  @PutMapping("/{id}/increase-stock")
  public ResponseEntity<MessageResponseDto> increaseStockProductById(
      @PathVariable("id") String id,
      @RequestBody @Valid ModifyStockRequestDto increase) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.increaseStock(uuid, increase.quantity());
    return ResponseEntity.ok(new MessageResponseDto("Successful increase for product with id: " + id));
  }

  @GetMapping("/{id}/availability")
  public ResponseEntity<CheckProductResponseDto> checkAvailabilityById(
      @PathVariable("id") String id,
      @RequestParam("quantity") @Min(1) int quantity) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    Product product = productUseCase.getProductByIdAndStatus(uuid, ProductStatus.ACTIVE);
    return ResponseEntity.ok(new CheckProductResponseDto(
        product.isAvailable(quantity),
        quantity,
        product.getStock()
    ));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteProductById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    productUseCase.softDeleteProductById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Product has been successfully deleted with id: " + id));
  }

  @PutMapping("/sku/{sku}/restore")
  public ResponseEntity<ProductResponseDto> restoreProductBySku(@PathVariable("sku") String sku) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.restoreProductBySku(sku)));
  }
}
