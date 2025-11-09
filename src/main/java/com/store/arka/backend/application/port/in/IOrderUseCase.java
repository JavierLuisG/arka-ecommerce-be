package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface IOrderUseCase {
  Order createOrder(UUID cartId);

  Order getOrderById(UUID id);

  List<Order> getAllOrders();

  List<Order> getAllOrdersByStatus(OrderStatus status);

  List<Order> getAllOrdersByCustomerId(UUID customerId);

  List<Order> getAllOrdersByItemsProductId(UUID productId);

  Order addOrderItemById(UUID id, UUID productId, Integer quantity);

  Order updateOrderItemQuantityById(UUID id, UUID productId, Integer quantity);

  Order removeOrderItemById(UUID id, UUID productId);

  void confirmOrderById(UUID id);

  void payOrderById(UUID id);

  void shippedOrderById(UUID id);

  void deliverOrderById(UUID id);

  void cancelOrderById(UUID id);
}
