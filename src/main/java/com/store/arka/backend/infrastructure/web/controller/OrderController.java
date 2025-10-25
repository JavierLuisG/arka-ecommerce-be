package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IOrderUseCase;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderResponseDto;
import com.store.arka.backend.infrastructure.web.dto.orderitem.request.UpdateQuantityToOrderItemDto;
import com.store.arka.backend.infrastructure.web.mapper.OrderDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/id/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.getOrderById(uuid)));
  }

  @GetMapping("/id/{id}/status/{status}")
  public ResponseEntity<OrderResponseDto> getOrderByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(mapper.toDto(orderUseCase.getOrderByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/id/{id}/customer/{customerId}")
  public ResponseEntity<OrderResponseDto> getOrderByIdAndCustomerId(
      @PathVariable("id") String id,
      @PathVariable("customerId") String customerId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.getOrderByIdAndCustomerId(uuid, customerUuid)));
  }

  @GetMapping("/id/{id}/customer/{customerId}/status/{status}")
  public ResponseEntity<OrderResponseDto> getOrderByIdAndCustomerIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("customerId") String customerId,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(
        mapper.toDto(orderUseCase.getOrderByIdAndCustomerIdAndStatus(uuid, customerUuid, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
    return ResponseEntity.ok(orderUseCase.getAllOrders().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByStatus(@PathVariable("status") String status) {
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(orderUseCase.getAllOrdersByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByCustomerId(@PathVariable("customerId") String customerId) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    return ResponseEntity.ok(orderUseCase.getAllOrdersByCustomerId(customerUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}/status/{status}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByCustomerIdAndStatus(
      @PathVariable("customerId") String customerId,
      @PathVariable("status") String status) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(orderUseCase.getAllOrdersByCustomerIdAndStatus(customerUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/productId/{productId}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByItemsProductId(@PathVariable("productId") String productId) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(orderUseCase.getAllOrdersByItemsProductId(productUuid)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/items/productId/{productId}/status/{status}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByItemsProductIdAndStatus(
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(orderUseCase.getAllOrdersByItemsProductIdAndStatus(productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/customerId/{customerId}/items/productId/{productId}/status/{status}")
  public ResponseEntity<List<OrderResponseDto>> getAllOrdersByCustomerIdAndItemsProductIdAndStatus(
      @PathVariable("customerId") String customerId,
      @PathVariable("productId") String productId,
      @PathVariable("status") String status) {
    UUID customerUuid = PathUtils.validateAndParseUUID(customerId);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    OrderStatus statusEnum = PathUtils.validateEnumOrThrow(OrderStatus.class, status, "OrderStatus");
    return ResponseEntity.ok(orderUseCase
        .getAllOrdersByCustomerIdAndItemsProductIdAndStatus(customerUuid, productUuid, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/id/{id}/productId/{productId}/add")
  public ResponseEntity<OrderResponseDto> addOrderItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToOrderItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.addOrderItemById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/id/{id}/productId/{productId}/update")
  public ResponseEntity<OrderResponseDto> updateOrderItemQuantityById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId,
      @RequestBody @Valid UpdateQuantityToOrderItemDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.updateOrderItemQuantityById(uuid, productUuid, dto.quantity())));
  }

  @PutMapping("/id/{id}/productId/{productId}/remove")
  public ResponseEntity<OrderResponseDto> removeOrderItemById(
      @PathVariable("id") String id,
      @PathVariable("productId") String productId) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    UUID productUuid = PathUtils.validateAndParseUUID(productId);
    return ResponseEntity.ok(mapper.toDto(orderUseCase.removeOrderItemById(uuid, productUuid)));
  }

  @PutMapping("/id/{id}/confirm")
  public ResponseEntity<MessageResponseDto> confirmOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.confirmOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully confirmed with id: " + id));
  }

  @PutMapping("/id/{id}/pay")
  public ResponseEntity<MessageResponseDto> payOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.payOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully paid with id: " + id));
  }

  @PutMapping("/id/{id}/shipped")
  public ResponseEntity<MessageResponseDto> shippedOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.shippedOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully shipped with id: " + id));
  }

  @PutMapping("/id/{id}/deliver")
  public ResponseEntity<MessageResponseDto> deliverOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.deliverOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully delivered with id: " + id));
  }

  @PutMapping("/id/{id}/cancel")
  public ResponseEntity<MessageResponseDto> cancelOrderById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    orderUseCase.cancelOrderById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Order has been successfully canceled with id: " + id));
  }
}
