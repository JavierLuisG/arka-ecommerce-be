package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface IOrderUseCase {
  Order createOrder(UUID cartId);

  Order getOrderById(UUID id);

  Order getOrderByIdSecure(UUID id);

  List<Order> getAllOrders();

  List<Order> getAllOrdersByStatus(OrderStatus status);

  List<Order> getAllOrdersByCustomerId(UUID customerId);

  List<Order> getAllOrdersByItemsProductId(UUID productId);

  Order addOrderItem(UUID id, UUID productId, Integer quantity);

  Order updateOrderItemQuantity(UUID id, UUID productId, Integer quantity);

  Order removeOrderItem(UUID id, UUID productId);

  void confirmOrder(UUID id);

  void payOrder(UUID id);

  void shippedOrder(UUID id);

  void deliverOrder(UUID id);

  void cancelOrder(UUID id);
}
