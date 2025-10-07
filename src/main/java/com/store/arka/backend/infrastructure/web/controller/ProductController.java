package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.InvalidEnumValueException;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.CreateProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.ModifyStockRequestDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateProductCategoriesDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateFieldsProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.CheckProductResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.ProductDtoMapper;
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

  @GetMapping("/id/{id}")
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductById(id)));
  }

  @GetMapping("/id/{id}/status/{status}")
  public ResponseEntity<ProductResponseDto> getProductByIdAndStatus(
      @PathVariable("id") UUID id, @PathVariable("status") String status) {
    ProductStatus statusEnum = parseStatusOrThrow(status);
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductByIdAndStatus(id, statusEnum)));
  }

  @GetMapping("/sku/{sku}")
  public ResponseEntity<ProductResponseDto> getProductIsNotDeletedBySku(@PathVariable("sku") String sku) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.getProductBySku(sku)));
  }

  @GetMapping
  public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
    return ResponseEntity.ok(productUseCase.getAllProducts().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<ProductResponseDto>> getAllProductsByStatus(@PathVariable("status") String status) {
    ProductStatus statusEnum = parseStatusOrThrow(status);
    return ResponseEntity.ok(productUseCase.getAllProductsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/id/{id}")
  public ResponseEntity<ProductResponseDto> putFieldsProduct(
      @PathVariable("id") UUID id, @RequestBody UpdateFieldsProductDto dto) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateFieldsProduct(id, mapper.toDomain(dto))));
  }

  @PutMapping("/id/{id}/categories")
  public ResponseEntity<ProductResponseDto> putProductCategories(
      @PathVariable("id") UUID id, @RequestBody UpdateProductCategoriesDto dto) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.updateCategories(id, dto.categories())));
  }

  @PutMapping("/id/{id}/decrease")
  public ResponseEntity<MessageResponseDto> decreaseStockProduct(
      @PathVariable("id") UUID id,
      @RequestBody @Valid ModifyStockRequestDto decrease) {
    productUseCase.decreaseStock(id, decrease.quantity());
    return ResponseEntity.ok(new MessageResponseDto("Successful decrease for product with id: " + id));
  }

  @PutMapping("/id/{id}/increase")
  public ResponseEntity<MessageResponseDto> increaseStockProduct(
      @PathVariable("id") UUID id,
      @RequestBody @Valid ModifyStockRequestDto increase) {
    productUseCase.increaseStock(id, increase.quantity());
    return ResponseEntity.ok(new MessageResponseDto("Successful increase for product with id: " + id));
  }

  @GetMapping("/id/{id}/availability")
  public ResponseEntity<CheckProductResponseDto> checkAvailabilityById(
      @PathVariable("id") UUID id,
      @RequestParam("quantity") @Min(1) int quantity) {
    Product product = productUseCase.getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    return ResponseEntity.ok(new CheckProductResponseDto(
        product.isAvailable(quantity),
        quantity,
        product.getStock()
    ));
  }

  @DeleteMapping("/id/{id}")
  public ResponseEntity<MessageResponseDto> deleteProduct(@PathVariable("id") UUID id) {
    productUseCase.deleteProductById(id);
    return ResponseEntity.ok(new MessageResponseDto("Product has been successfully deleted with id: " + id));
  }

  @PutMapping("/sku/{sku}/restore")
  public ResponseEntity<ProductResponseDto> restoreProduct(@PathVariable("sku") String name) {
    return ResponseEntity.ok(mapper.toDto(productUseCase.restoreProductBySku(name)));
  }

  private ProductStatus parseStatusOrThrow(String status) {
    try {
      return ProductStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidEnumValueException("Invalid ProductStatus: " + status);
    }
  }
}
