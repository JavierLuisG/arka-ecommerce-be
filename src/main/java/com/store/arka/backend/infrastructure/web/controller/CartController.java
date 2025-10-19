package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ICartUseCase;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.cart.request.CreateCartDto;
import com.store.arka.backend.infrastructure.web.dto.cart.response.CartResponseDto;
import com.store.arka.backend.infrastructure.web.dto.cartitem.request.UpdateQuantityToCartItemDto;
import com.store.arka.backend.infrastructure.web.mapper.CartDtoMapper;
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
@RequestMapping("/api/carts")
public class CartController {
  private final ICartUseCase cartUseCase;
  private final CartDtoMapper mapper;

  @PostMapping
  public ResponseEntity<CartResponseDto> postCart(@RequestBody @Valid CreateCartDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(cartUseCase.createCart(mapper.toDomain(dto), dto.customerId())));
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<CartResponseDto> getCartById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.getCartById(uuid)));
  }

  @GetMapping("/id/{id}/status/{status}")
  public ResponseEntity<CartResponseDto> getCartByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(mapper.toDto(cartUseCase.getCartByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/id/{id}/customer/{customerId}")
  public ResponseEntity<CartResponseDto> getCartByIdAndCustomerId(
      @PathVariable("id") String id,
      @PathVariable("customerId") String customerId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.getCartByIdAndCustomerId(uuid, customerUuid)));
  }

  @GetMapping("/id/{id}/customer/{customerId}/status/{status}")
  public ResponseEntity<CartResponseDto> getCartByCustomerIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("customerId") String customerId,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(
        mapper.toDto(cartUseCase.getCartByIdAndCustomerIdAndStatus(uuid, customerUuid, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<CartResponseDto>> getAllCarts() {
    return ResponseEntity.ok(cartUseCase.getAllCarts().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByStatus(@PathVariable("status") String status) {
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(cartUseCase.getAllCartsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByCustomerId(@PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(cartUseCase.getAllCartsByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}/status/{status}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByCustomerIdAndStatus(
      @PathVariable("customerId") String customerId,
      @PathVariable("status") String status) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(cartUseCase.getAllCartsByCustomerIdAndStatus(customerUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/productId/{productId}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(cartUseCase.getAllCartsByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/productId/{productId}/status/{status}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByItemsProductIdAndStatus(
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(cartUseCase.getAllCartsByItemsProductIdAndStatus(productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}/items/productId/{productId}/status/{status}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByCustomerIdAndItemsProductIdAndStatus(
      @PathVariable("customerId") String customerId,
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(
        cartUseCase
            .getAllCartsByCustomerIdAndItemsProductIdAndStatus(customerUuid, productUuid, statusEnum)
            .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/id/{id}/productId/{productId}/addCartItem")
  public ResponseEntity<CartResponseDto> addCartItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToCartItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.addCartItemById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/id/{id}/productId/{productId}/updateCartItem")
  public ResponseEntity<CartResponseDto> updateCartItemQuantityById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToCartItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.updateCartItemQuantityById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/id/{id}/productId/{productId}/removeCartItem")
  public ResponseEntity<CartResponseDto> removeCartItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.removeCartItemById(uuid, productUuid)));
  }

  @PutMapping("/id/{id}/emptyCartItems")
  public ResponseEntity<CartResponseDto> emptyCartItemsById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.emptyCartItemsById(uuid)));
  }

  @PutMapping("/id/{id}/confirm")
  public ResponseEntity<MessageResponseDto> confirmCartById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    cartUseCase.confirmedCartById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Cart has been successfully confirmed with id: " + id));
  }

  @DeleteMapping("/id/{id}")
  public ResponseEntity<MessageResponseDto> deleteCartById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    cartUseCase.deleteCartById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Cart has been successfully deleted with id: " + id));
  }
}
