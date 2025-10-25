package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.enums.OrderStatus;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderUseCase {
  private final IOrderAdapterPort orderAdapterPort;
  private final IOrderItemUseCase orderItemUseCase;
  private final ICartAdapterPort cartAdapterPort;
  private final IProductUseCase productUseCase;
  private final ICustomerUseCase customerUseCase;

  @Override
  @Transactional
  public Order createOrder(UUID cartId) {
    Cart cartFound = findCartOrThrow(cartId);
    if (cartFound.getItems().isEmpty()) throw new ItemsEmptyException("Cart items in Order cannot be empty");
    if (orderAdapterPort.existsByCartId(cartId)) throw new BusinessException("An order already exists for this Cart");
    Customer customerFound = findCustomerOrThrow(cartFound.getCustomer().getId());
    List<OrderItem> orderItems = new ArrayList<>();
    cartFound.getItems().forEach(cartItem -> {
      orderItems.add(OrderItem.create(cartItem.getProduct(), cartItem.getQuantity()));
    });
    Order created = Order.create(cartId, customerFound, orderItems);
    return orderAdapterPort.saveCreateOrder(created);
  }

  @Override
  @Transactional
  public Order getOrderById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return orderAdapterPort.findOrderById(id)
        .orElseThrow(() -> new ModelNotFoundException("Order with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Order getOrderByIdAndStatus(UUID id, OrderStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return orderAdapterPort.findOrderByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Order with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public Order getOrderByIdAndCustomerId(UUID id, UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findOrderByIdAndCustomerId(id, customerId)
        .orElseThrow(() -> new ModelNotFoundException(
            "Order with id " + id + " and customerId " + customerId + " not found"));
  }

  @Override
  @Transactional
  public Order getOrderByIdAndCustomerIdAndStatus(UUID id, UUID customerId, OrderStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findOrderByIdAndCustomerIdAndStatus(id, customerId, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Order with id " + id + ", customerId " + customerId + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Order> getAllOrders() {
    return orderAdapterPort.findAllOrders();
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByStatus(OrderStatus status) {
    return orderAdapterPort.findAllOrdersByStatus(status);
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByCustomerId(UUID customerId) {
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findAllOrdersByCustomerId(customerId);
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findAllOrdersByCustomerIdAndStatus(customerId, status);
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    return orderAdapterPort.findAllOrdersByItemsProductId(productId);
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByItemsProductIdAndStatus(UUID productId, OrderStatus status) {
    findProductOrThrow(productId);
    return orderAdapterPort.findAllOrdersByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional
  public List<Order> getAllOrdersByCustomerIdAndItemsProductIdAndStatus(
      UUID customerId, UUID productId, OrderStatus status) {
    findCustomerOrThrow(customerId);
    findProductOrThrow(productId);
    return orderAdapterPort.findAllOrdersByCustomerIdAndItemsProductIdAndStatus(customerId, productId, status);
  }

  @Override
  @Transactional
  public Order addOrderItemById(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (orderFound.containsProduct(productId)) {
      OrderItem orderItem = findOrderItemInOrderOrThrow(productId, orderFound);
      orderItemUseCase.addQuantityById(orderItem.getId(), quantity);
    } else {
      OrderItem newItem = OrderItem.create(productFound, quantity);
      orderFound.getItems().add(newItem);
      orderItemUseCase.addOrderItem(orderFound.getId(), newItem);
    }
    orderFound.recalculateTotal();
    return orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public Order updateOrderItemQuantityById(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOrderException("Product not found in Order id " + orderFound.getId());
    }
    OrderItem orderItem = findOrderItemInOrderOrThrow(productFound.getId(), orderFound);
    orderItemUseCase.updateQuantity(orderItem.getId(), quantity);
    Order orderUpdated = getOrderById(id);
    orderUpdated.recalculateTotal();
    return orderAdapterPort.saveUpdateOrder(orderUpdated);
  }

  @Override
  @Transactional
  public Order removeOrderItemById(UUID id, UUID productId) {
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOrderException("Product not found in Order id " + orderFound.getId());
    }
    orderFound.removeOrderItem(productFound);
    return orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void confirmOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.confirm();
    orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void payOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.pay();
    orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void shippedOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.shipped();
    orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void deliverOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.deliver();
    orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void cancelOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.cancel();
    orderAdapterPort.saveUpdateOrder(orderFound);
  }

  private Cart findCartOrThrow(UUID cartId) {
    if (cartId == null) throw new InvalidArgumentException("CartId in Order cannot be null");
    return cartAdapterPort.findCartByIdAndStatus(cartId, CartStatus.CHECKED_OUT)
        .orElseThrow(() -> new ModelNotFoundException("Cart must be CHECKED_OUT to create an Order"));
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    if (customerId == null) throw new InvalidArgumentException("CustomerId in Order cannot be null");
    return customerUseCase.getCustomerByIdAndStatus(customerId, CustomerStatus.ACTIVE);
  }

  private Product findProductOrThrow(UUID productId) {
    if (productId == null) throw new InvalidArgumentException("ProductId in Order cannot be null");
    return productUseCase.getProductByIdAndStatus(productId, ProductStatus.ACTIVE);
  }

  private static OrderItem findOrderItemInOrderOrThrow(UUID productId, Order orderFound) {
    return orderFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInOrderException(
            "Product " + productId + " not found in Order " + orderFound.getId()));
  }
}
