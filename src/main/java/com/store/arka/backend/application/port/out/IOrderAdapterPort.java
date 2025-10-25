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

  Optional<Order> findOrderByIdAndStatus(UUID id, OrderStatus status);

  Optional<Order> findOrderByIdAndCustomerId(UUID id, UUID customerId);

  Optional<Order> findOrderByIdAndCustomerIdAndStatus(UUID id, UUID customerId, OrderStatus status);

  List<Order> findAllOrders();

  List<Order> findAllOrdersByStatus(OrderStatus status);

  List<Order> findAllOrdersByCustomerId(UUID customerId);

  List<Order> findAllOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status);

  List<Order> findAllOrdersByItemsProductId(UUID productId);

  List<Order> findAllOrdersByItemsProductIdAndStatus(UUID productId, OrderStatus status);

  List<Order> findAllOrdersByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, OrderStatus status);

  boolean existsByCartId(UUID cartId);
}
