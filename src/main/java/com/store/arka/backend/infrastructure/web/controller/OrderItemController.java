package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IOrderItemUseCase;
import com.store.arka.backend.infrastructure.web.dto.orderitem.response.OrderItemResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.OrderItemDtoMapper;
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
@RequestMapping("/api/order_items")
public class OrderItemController {
  private final IOrderItemUseCase orderItemUseCase;
  private final OrderItemDtoMapper mapper;

  @GetMapping("/id/{id}")
  public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(orderItemUseCase.getOrderItemById(uuid)));
  }

  @GetMapping
  public ResponseEntity<List<OrderItemResponseDto>> getAllOrderItems() {
    return ResponseEntity.ok(orderItemUseCase.getAllOrderItems()
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/productId/{productId}")
  public ResponseEntity<List<OrderItemResponseDto>> getAllOrderItemsByProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(orderItemUseCase.getAllOrderItemsByProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }
}
