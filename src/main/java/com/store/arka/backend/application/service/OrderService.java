package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
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
  private final INotificationUseCase notificationUseCase;

  @Override
  @Transactional
  public Order createOrder(UUID cartId) {
    if (orderAdapterPort.existsByCartId(cartId)) throw new BusinessException("An order already exists for this Cart");
    Cart cartFound = findCartOrThrow(cartId);
    if (cartFound.getItems().isEmpty()) throw new ItemsEmptyException("Cart items in Order cannot be empty");
    Customer customerFound = findCustomerOrThrow(cartFound.getCustomer().getId());
    List<OrderItem> orderItems = new ArrayList<>();
    cartFound.getItems().forEach(cartItem -> {
      productUseCase.validateAvailabilityOrThrow(cartItem.getProductId(), cartItem.getQuantity());
      orderItems.add(OrderItem.create(cartItem.getProduct(), cartItem.getQuantity()));
    });
    Order created = Order.create(cartId, customerFound, orderItems);
    return orderAdapterPort.saveCreateOrder(created);
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    return orderAdapterPort.findOrderById(id)
        .orElseThrow(() -> new ModelNotFoundException("Order ID " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderByIdAndStatus(UUID id, OrderStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    return orderAdapterPort.findOrderByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Order ID " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderByIdAndCustomerId(UUID id, UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findOrderByIdAndCustomerId(id, customerId)
        .orElseThrow(() -> new ModelNotFoundException(
            "Order ID " + id + " and customer ID " + customerId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderByIdAndCustomerIdAndStatus(UUID id, UUID customerId, OrderStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findOrderByIdAndCustomerIdAndStatus(id, customerId, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Order ID " + id + ", customer ID " + customerId + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    return orderAdapterPort.findAllOrders();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByStatus(OrderStatus status) {
    return orderAdapterPort.findAllOrdersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByCustomerId(UUID customerId) {
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findAllOrdersByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
    findCustomerOrThrow(customerId);
    return orderAdapterPort.findAllOrdersByCustomerIdAndStatus(customerId, status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    return orderAdapterPort.findAllOrdersByItemsProductId(productId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByItemsProductIdAndStatus(UUID productId, OrderStatus status) {
    findProductOrThrow(productId);
    return orderAdapterPort.findAllOrdersByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional(readOnly = true)
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
      OrderItem orderItem = findOrderItemOrThrow(productId, orderFound);
      orderItemUseCase.addQuantityById(orderItem.getId(), quantity);
      orderFound = getOrderById(id);
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
    productUseCase.validateAvailabilityOrThrow(productId, quantity);
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    OrderItem orderItem = findOrderItemOrThrow(productFound.getId(), orderFound);
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
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    orderFound.removeOrderItem(productFound);
    return orderAdapterPort.saveUpdateOrder(orderFound);
  }

  @Override
  @Transactional
  public void confirmOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.decreaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.confirm();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CONFIRMED));
  }

  @Override
  @Transactional
  public void payOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.pay();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_PAID));
  }

  @Override
  @Transactional
  public void shippedOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.shipped();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_SHIPPED));
  }

  @Override
  @Transactional
  public void deliverOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.deliver();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_DELIVERED));
  }

  @Override
  @Transactional
  public void cancelOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.increaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.cancel();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CANCELED));
  }

  private Cart findCartOrThrow(UUID cartId) {
    if (cartId == null) throw new InvalidArgumentException("Cart ID in Order cannot be null");
    return cartAdapterPort.findCartByIdAndStatus(cartId, CartStatus.CHECKED_OUT)
        .orElseThrow(() -> new ModelNotFoundException("Cart must be CHECKED_OUT to create an Order"));
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    if (customerId == null) throw new InvalidArgumentException("Customer ID in Order cannot be null");
    Customer customer = customerUseCase.getCustomerById(customerId);
    customer.throwIfDeleted();
    return customer;
  }

  private Product findProductOrThrow(UUID productId) {
    if (productId == null) throw new InvalidArgumentException("Product ID in Order cannot be null");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private static OrderItem findOrderItemOrThrow(UUID productId, Order orderFound) {
    return orderFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInOperationException(
            "Product ID " + productId + " not found in Order " + orderFound.getId()));
  }
}
