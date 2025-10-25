package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderItemAdapterPort {
  OrderItem saveAddOrderItem(UUID orderId, OrderItem orderItem);

  OrderItem saveUpdateOrderItem(OrderItem orderItem);

  Optional<OrderItem> findOrderItemById(UUID id);

  List<OrderItem> findAllOrderItems();

  List<OrderItem> findAllOrderItemsByProductId(UUID productId);
}
