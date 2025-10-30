package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.infrastructure.web.dto.cart.response.CartItemResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.CartItemDtoMapper;
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
@RequestMapping("/api/cart-items")
public class CartItemController {
  private final ICartItemUseCase cartItemUseCase;
  private final CartItemDtoMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<CartItemResponseDto> getCartItemById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(cartItemUseCase.getCartItemById(uuid)));
  }

  @GetMapping
  public ResponseEntity<List<CartItemResponseDto>> getAllCartItems() {
    return ResponseEntity.ok(cartItemUseCase.getAllCartItems()
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<CartItemResponseDto>> getAllCartItemsByProductId(
      @PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(cartItemUseCase.getAllCartItemsByProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }
}
