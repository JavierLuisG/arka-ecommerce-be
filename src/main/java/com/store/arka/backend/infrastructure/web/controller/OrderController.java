package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IOrderUseCase;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderResponseDto;
import com.store.arka.backend.infrastructure.web.dto.order.request.UpdateQuantityToOrderItemDto;
import com.store.arka.backend.infrastructure.web.mapper.OrderDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
  private final IOrderUseCase orderUseCase;
  private final OrderDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.getOrderById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getAllOrders(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(orderUseCase.getAllOrders().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(orderUseCase.getAllOrdersByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByCustomerId(@PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(orderUseCase.getAllOrdersByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/items/product/{productId}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(orderUseCase.getAllOrdersByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/add-item")
  public ResponseEntity<OrderResponseDto> addOrderItem(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToOrderItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.addOrderItemById(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/update-item-quantity")
  public ResponseEntity<OrderResponseDto> updateOrderItemQuantity(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToOrderItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.updateOrderItemQuantityById(uuid, productUuid, dto.quantity())));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/product/{productId}/remove-item")
  public ResponseEntity<OrderResponseDto> removeOrderItem(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.removeOrderItemById(uuid, productUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/confirm")
  public ResponseEntity<MessageResponseDto> confirmOrder(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.confirmOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully confirmed with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/pay")
  public ResponseEntity<MessageResponseDto> payOrder(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.payOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully paid with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/shipped")
  public ResponseEntity<MessageResponseDto> shippedOrder(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.shippedOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully shipped with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/deliver")
  public ResponseEntity<MessageResponseDto> deliverOrder(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.deliverOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully delivered with ID " + id));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/cancel")
  public ResponseEntity<MessageResponseDto> cancelOrder(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.cancelOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully canceled with ID " + id));
  }
}
