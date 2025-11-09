package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ICartUseCase;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.cart.request.CreateCartDto;
import com.store.arka.backend.infrastructure.web.dto.cart.response.CartResponseDto;
import com.store.arka.backend.infrastructure.web.dto.cart.request.UpdateQuantityToCartItemDto;
import com.store.arka.backend.infrastructure.web.mapper.CartDtoMapper;
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
@RequestMapping("/api/carts")
public class CartController {
  private final ICartUseCase cartUseCase;
  private final CartDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PostMapping
  public ResponseEntity<CartResponseDto> postCart(@RequestBody @Valid CreateCartDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(cartUseCase.createCart(mapper.toDomain(dto), dto.customerId())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<CartResponseDto> getCartById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.getCartById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<CartResponseDto>> getAllCarts(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(cartUseCase.getAllCarts().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    CartStatus statusEnum = PathUtils.validateEnumOrThrow(CartStatus.class, status, "CartStatus");
    return ResponseEntity.ok(cartUseCase.getAllCartsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByCustomerId(@PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(cartUseCase.getAllCartsByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/items/product/{productId}")
  public ResponseEntity<List<CartResponseDto>> getAllCartsByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(cartUseCase.getAllCartsByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/add-item")
  public ResponseEntity<CartResponseDto> addCartItem(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToCartItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.addCartItem(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/update-item-quantity")
  public ResponseEntity<CartResponseDto> updateCartItemQuantity(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToCartItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.updateCartItemQuantity(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/remove-item")
  public ResponseEntity<CartResponseDto> removeCartItem(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.removeCartItem(uuid, productUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/empty-items")
  public ResponseEntity<CartResponseDto> emptyCartItems(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(cartUseCase.emptyCartItems(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/checkout")
  public ResponseEntity<MessageResponseDto> checkoutCart(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    String response = cartUseCase.checkoutCart(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Cart has been successfully confirmed with ID " + id + ". " +
        "Order created with ID " + response));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> deleteCart(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    cartUseCase.deleteCart(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Cart has been successfully deleted with ID " + id));
  }
}
