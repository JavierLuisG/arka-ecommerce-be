package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderAdapterPort {
  Order saveCreateOrder(Order order);

  Order saveUpdateOrder(Order order);

  Optional<Order> findOrderById(UUID id);

  List<Order> findAllOrders();

  List<Order> findAllOrdersByStatus(OrderStatus status);

  List<Order> findAllOrdersByCustomerId(UUID customerId);

  List<Order> findAllOrdersByItemsProductId(UUID productId);

  boolean existsByCartId(UUID cartId);
}
