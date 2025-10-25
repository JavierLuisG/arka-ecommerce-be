package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface IOrderItemUseCase {
  OrderItem addOrderItem(UUID orderId, OrderItem orderItem);

  OrderItem getOrderItemById(UUID id);

  List<OrderItem> getAllOrderItems();

  List<OrderItem> getAllOrderItemsByProductId(UUID productId);

  OrderItem addQuantityById(UUID id, Integer quantity);

  OrderItem updateQuantity(UUID id, Integer quantity);
}
